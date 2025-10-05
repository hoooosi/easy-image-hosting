package io.github.hoooosi.imagehosting.manager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.hoooosi.imagehosting.constant.TopicNames;
import io.github.hoooosi.imagehosting.entity.ImageFile;
import io.github.hoooosi.imagehosting.entity.ImageItem;
import io.github.hoooosi.imagehosting.entity.Space;
import io.github.hoooosi.imagehosting.exception.BusinessException;
import io.github.hoooosi.imagehosting.exception.ErrorCode;
import io.github.hoooosi.imagehosting.mapper.ImageItemBaseMapper;
import io.github.hoooosi.imagehosting.mapper.SpaceBaseMapper;
import io.github.hoooosi.imagehosting.utils.ImageUtils;
import io.github.hoooosi.imagehosting.utils.ThrowUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

@Component
@Slf4j
@AllArgsConstructor
public class ImageManager {
    private final MinioManager minioManager;
    private final ImageItemBaseMapper imageItemBaseMapper;
    private final SpaceBaseMapper spaceBaseMapper;
    private final TransactionTemplate transactionTemplate;

    @Async
    public void uploadProcess(MultipartFile multipartFile, ImageItem imageItem) {
        log.info("Start process image upload, imageItem: {}", imageItem);

        try (InputStream is = multipartFile.getInputStream()) {
            log.info("Start upload file to minio, imageItem: {}", imageItem);
            String contentType = multipartFile.getContentType();
            ImageFile imageFile = minioManager.uploadToMinio(is.readAllBytes(), contentType);
            this.saveToDb(imageItem.setStatus(ImageItem.Status.SUCCESS)
                    .setMd5(imageFile.getMd5())
                    .setHeight(imageFile.getHeight())
                    .setWidth(imageFile.getWidth())
                    .setSize(imageFile.getSize())
                    .setFileId(imageFile.getId()));

            log.info("Upload image success, imageItem: {}", imageItem);
        } catch (Exception e) {
            log.error("Upload image failed, imageItem: {}", imageItem.getId(), e);
            imageItemBaseMapper.update(Wrappers
                    .lambdaUpdate(ImageItem.class)
                    .eq(ImageItem::getId, imageItem.getId())
                    .set(ImageItem::getStatus, ImageItem.Status.FAILED));
        }
    }

    @Async
    public void convertProcess(ImageItem imageItem, String objectName) {
        log.info("Start process image convert, imageItem: {}", imageItem);

        try (InputStream is = minioManager.download(objectName)) {
            String contentType = imageItem.getContentType();
            BufferedImage image = ImageIO.read(is);
            byte[] bytes = ImageUtils.convertToBytes(image, contentType);
            ImageFile imageFile = minioManager.uploadToMinio(bytes, contentType);
            this.saveToDb(imageItem.setStatus(ImageItem.Status.SUCCESS)
                    .setMd5(imageFile.getMd5())
                    .setHeight(imageFile.getHeight())
                    .setWidth(imageFile.getWidth())
                    .setSize(imageFile.getSize())
                    .setFileId(imageFile.getId()));

            log.info("Convert image success, imageItem: {}", imageItem);
        } catch (Exception e) {
            log.error("Convert image failed, imageItem: {}", imageItem.getId(), e);
            imageItemBaseMapper.update(Wrappers
                    .lambdaUpdate(ImageItem.class)
                    .eq(ImageItem::getId, imageItem.getId())
                    .set(ImageItem::getStatus, ImageItem.Status.FAILED));
        }
    }

    protected void saveToDb(ImageItem imageItem) {
        transactionTemplate.executeWithoutResult(status -> {
            // Check space capacity
            Space space = spaceBaseMapper.selectOne(Wrappers.lambdaQuery(Space.class)
                    .select(Space::getTotalSize, Space::getMaxSize)
                    .eq(Space::getId, imageItem.getSpaceId()));

            // Check and update space capacity
            ThrowUtils.throwIfZero(spaceBaseMapper.update(Wrappers
                    .lambdaUpdate(Space.class)
                    .setSql("total_size = total_size + {0}", imageItem.getSize())
                    .eq(Space::getId, imageItem.getSpaceId())
                    .le(Space::getTotalSize, space.getMaxSize() - imageItem.getSize())), ErrorCode.INSUFFICIENT_CAPACITY);

            // Update image item
            ThrowUtils.throwIfZero(imageItemBaseMapper.updateById(imageItem), ErrorCode.DATA_SAVE_ERROR);
        });
    }
}
