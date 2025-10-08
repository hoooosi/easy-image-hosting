package io.github.hoooosi.imagehosting.listener;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.hoooosi.imagehosting.common.ConvertMessage;
import io.github.hoooosi.imagehosting.constant.TopicNames;
import io.github.hoooosi.imagehosting.entity.ImageFile;
import io.github.hoooosi.imagehosting.entity.ImageItem;
import io.github.hoooosi.imagehosting.exception.ErrorCode;
import io.github.hoooosi.imagehosting.manager.MinioManager;
import io.github.hoooosi.imagehosting.manager.SpaceManager;
import io.github.hoooosi.imagehosting.mapper.ImageFileBaseMapper;
import io.github.hoooosi.imagehosting.mapper.ImageItemBaseMapper;
import io.github.hoooosi.imagehosting.utils.ImageItemUtils;
import io.github.hoooosi.imagehosting.utils.ImageUtils;
import io.github.hoooosi.imagehosting.utils.ThrowUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

@Slf4j
@Component
@AllArgsConstructor
@RocketMQMessageListener(topic = TopicNames.FORMAT_CONVERT_TOPIC, consumerGroup = "consumer-group")
public class FormatConvertListener implements RocketMQListener<ConvertMessage> {
    private final MinioManager minioManager;
    private final ImageFileBaseMapper imageFileBaseMapper;
    private final ImageItemUtils imageItemUtils;
    private final ImageItemBaseMapper imageItemBaseMapper;
    private final TransactionTemplate transactionTemplate;
    private final SpaceManager spaceManager;

    @Override
    public void onMessage(ConvertMessage message) {
        log.info("Start process image convert, message: {}", message);

        String objectKey = message.getObjectKey();
        try (InputStream is = minioManager.download(objectKey)) {
            String contentType = message.getContentType();
            BufferedImage image = ImageIO.read(is);
            BufferedImage thumbnailImage = ImageUtils.generateThumbnail(image);

            byte[] bytes = ImageUtils.convertToBytes(image, contentType);
            byte[] thumbnailBytes = ImageUtils.convertToBytes(thumbnailImage, contentType);
            minioManager.upload(bytes, objectKey, contentType);
            minioManager.upload(thumbnailBytes, "thumbnail/" + objectKey, contentType);

            ImageFile imageFile = imageFileBaseMapper.selectOne(Wrappers.lambdaQuery(ImageFile.class)
                    .eq(ImageFile::getMd5, objectKey));
            if (imageFile == null) {
                imageFile = new ImageFile()
                        .setSize((long) bytes.length)
                        .setWidth(image.getWidth())
                        .setHeight(image.getHeight())
                        .setMd5(objectKey)
                        .setContentType(contentType);
            } else ThrowUtils.throwIfZero(imageFileBaseMapper.insert(imageFile), ErrorCode.DATA_SAVE_ERROR);

            ImageItem imageItem = imageItemBaseMapper.selectOne(Wrappers.lambdaQuery(ImageItem.class)
                    .eq(ImageItem::getId, message.getItemId())
                    .eq(ImageItem::getStatus, ImageItem.Status.PROCESSING));
            try {
                ImageFile finalImageFile = imageFile;
                transactionTemplate.executeWithoutResult(status -> {
                    // Merge image item and file
                    imageItemUtils.union(imageItem, finalImageFile);
                    ThrowUtils.throwIfZero(spaceManager.adjustingUsedCapacity(imageItem.getSpaceId(), bytes.length), ErrorCode.INSUFFICIENT_CAPACITY);
                    ThrowUtils.throwIfZero(imageItemBaseMapper.updateById(imageItem), ErrorCode.DATA_SAVE_ERROR);
                });
            } catch (Exception e) {
                imageItemBaseMapper.update(Wrappers.lambdaUpdate(ImageItem.class)
                        .eq(ImageItem::getId, imageItem.getId())
                        .set(ImageItem::getStatus, ImageItem.Status.FAILED));
            }
        } catch (Exception e) {
            log.error("Convert image failed, imageItem: {}", message.getItemId(), e);
        }
    }
}
