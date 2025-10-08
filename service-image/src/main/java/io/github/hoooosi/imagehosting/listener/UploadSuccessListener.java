package io.github.hoooosi.imagehosting.listener;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.hoooosi.imagehosting.utils.ImageItemUtils;
import io.github.hoooosi.imagehosting.constant.TopicNames;
import io.github.hoooosi.imagehosting.entity.ImageFile;
import io.github.hoooosi.imagehosting.entity.ImageItem;
import io.github.hoooosi.imagehosting.exception.ErrorCode;
import io.github.hoooosi.imagehosting.manager.ImageManager;
import io.github.hoooosi.imagehosting.manager.MinioManager;
import io.github.hoooosi.imagehosting.manager.SpaceManager;
import io.github.hoooosi.imagehosting.mapper.ImageFileBaseMapper;
import io.github.hoooosi.imagehosting.mapper.ImageItemBaseMapper;
import io.github.hoooosi.imagehosting.mapper.SpaceBaseMapper;
import io.github.hoooosi.imagehosting.utils.ImageUtils;
import io.github.hoooosi.imagehosting.utils.ThrowUtils;
import io.minio.StatObjectResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
@RocketMQMessageListener(topic = TopicNames.UPLOAD_TOPIC, selectorExpression = "success", consumerGroup = "consumer-group")
public class UploadSuccessListener implements RocketMQListener<String> {

    private final ImageFileBaseMapper imageFileBaseMapper;
    private final MinioManager minioManager;
    private final TransactionTemplate transactionTemplate;
    private final ImageItemBaseMapper imageItemBaseMapper;
    private final ImageItemUtils imageItemUtils;
    private final SpaceManager spaceManager;

    @Override
    public void onMessage(String objectKey) {
        log.info("Received upload success message: {}", objectKey);
        try (InputStream is = minioManager.download(objectKey)) {
            StatObjectResponse stat = minioManager.stat(objectKey);
            BufferedImage image = ImageIO.read(is);
            BufferedImage thumbnailImage = ImageUtils.generateThumbnail(image);

            String contentType = stat.contentType();
            int width = image.getWidth();
            int height = image.getHeight();
            long size = stat.size();

            byte[] thumbnailBytes = ImageUtils.convertToBytes(thumbnailImage, contentType);
            minioManager.upload(thumbnailBytes, "thumbnail/" + objectKey, contentType);

            ImageFile imageFile = new ImageFile()
                    .setSize(size)
                    .setWidth(width)
                    .setHeight(height)
                    .setMd5(objectKey)
                    .setContentType(contentType);

            ThrowUtils.throwIfZero(imageFileBaseMapper.insert(imageFile), ErrorCode.DATA_SAVE_ERROR);

            List<ImageItem> imageItems = imageItemBaseMapper.selectList(Wrappers.lambdaQuery(ImageItem.class)
                    .eq(ImageItem::getMd5, objectKey)
                    .eq(ImageItem::getStatus, ImageItem.Status.PROCESSING));

            for (ImageItem imageItem : imageItems) {
                try {
                    transactionTemplate.executeWithoutResult(status -> {
                        // Merge image item and file
                        imageItemUtils.union(imageItem, imageFile);
                        ThrowUtils.throwIfZero(spaceManager.adjustingUsedCapacity(imageItem.getSpaceId(), size), ErrorCode.INSUFFICIENT_CAPACITY);
                        ThrowUtils.throwIfZero(imageItemBaseMapper.updateById(imageItem), ErrorCode.DATA_SAVE_ERROR);
                    });
                } catch (Exception e) {
                    imageItemBaseMapper.update(Wrappers.lambdaUpdate(ImageItem.class)
                            .eq(ImageItem::getId, imageItem.getId())
                            .set(ImageItem::getStatus, ImageItem.Status.FAILED));
                }

            }
        } catch (Exception e) {
            imageItemBaseMapper.update(Wrappers.lambdaUpdate(ImageItem.class)
                    .eq(ImageItem::getMd5, objectKey)
                    .set(ImageItem::getStatus, ImageItem.Status.FAILED));
            log.error("Failed to process message", e);
        }
    }


}
