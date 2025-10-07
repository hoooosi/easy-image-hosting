package io.github.hoooosi.imagehosting.manager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.hoooosi.imagehosting.constant.TopicNames;
import io.github.hoooosi.imagehosting.entity.ImageFile;
import io.github.hoooosi.imagehosting.exception.BusinessException;
import io.github.hoooosi.imagehosting.exception.ErrorCode;
import io.github.hoooosi.imagehosting.mapper.ImageFileBaseMapper;
import io.github.hoooosi.imagehosting.utils.ThrowUtils;
import io.github.hoooosi.imagehosting.property.MinioProperty;
import io.github.hoooosi.imagehosting.utils.ImageUtils;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@AllArgsConstructor
public class MinioManager {
    private final MinioClient minioClient;
    private final MinioProperty property;
    private final ImageFileBaseMapper imageFileBaseMapper;

    @PostConstruct
    public void init() throws Exception {
        try {
            ThrowUtils.throwIf(!minioClient.bucketExists(BucketExistsArgs
                    .builder()
                    .bucket(property.getBucketName())
                    .build()), ErrorCode.PARAMS_ERROR, "Bucket does not exist");
        } catch (Exception e) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(property.getBucketName()).build());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ImageFile uploadToMinio(byte[] bytes, String contentType) {
        try {
            String md5 = DigestUtils.md5Hex(bytes);
            ImageFile imageFile = imageFileBaseMapper.selectOne(Wrappers
                    .lambdaQuery(ImageFile.class)
                    .eq(ImageFile::getMd5, md5));

            if (Objects.isNull(imageFile)) {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
                BufferedImage thumbnailImage = ImageUtils.generateThumbnail(image);

                long size = this.upload(bytes, md5, contentType);
                long thumbnailSize = this.upload(ImageUtils.convertToBytes(thumbnailImage, contentType), "thumbnail/" + md5, contentType);

                imageFile = new ImageFile()
                        .setMd5(md5)
                        .setContentType(contentType)
                        .setSize(size + thumbnailSize)
                        .setWidth(image.getWidth())
                        .setHeight(image.getHeight());

                ThrowUtils.throwIfZero(imageFileBaseMapper.insert(imageFile), ErrorCode.DATA_SAVE_ERROR);
            }
            return imageFile;
        } catch (Exception e) {
            throw new RuntimeException("Upload file to MinIO failed: " + e);
        }
    }

    public long upload(byte[] bytes, String objectName, String contentType) {
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(property.getBucketName())
                            .object(objectName)
                            .stream(is, -1, 10485760)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Upload file to MinIO failed: " + objectName, e);
        }
        return bytes.length;
    }

    public void delete(String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(property.getBucketName())
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Delete file from MinIO failed: " + objectName, e);
        }
    }

    public void delete(List<String> objectNames) {
        try {
            List<DeleteObject> objectsToDelete = new ArrayList<>();
            objectNames.forEach(objectName -> {
                objectsToDelete.add(new DeleteObject(objectName));
            });
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(property.getBucketName())
                            .objects(objectsToDelete)
                            .build());
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                log.error("Failed to delete object: {}, error: {}", error.objectName(), error.message());
            }
        } catch (Exception e) {
            throw new RuntimeException("Delete file from MinIO failed: " + e);
        }
    }

    public InputStream download(String objectName) {
        log.info("Download file: {}", objectName);
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(property.getBucketName())
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Download file from MinIO failed: " + objectName, e);
        }
    }

    public StatObjectResponse stat(String objectName) {
        try {
            return minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(property.getBucketName())
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Stat object failed: " + objectName, e);
        }
    }

    public String generateTemporaryLink(String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(property.getBucketName())
                            .object(objectName)
                            .expiry(10, TimeUnit.MINUTES)
                            .build());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }


    public PresignedPostForm createPresignedPost(String md5, String contentType, int bytes, int minutes) {
        try {
            PostPolicy policy = new PostPolicy(property.getBucketName(), ZonedDateTime.now().plusMinutes(minutes));
            policy.addEqualsCondition("key", md5);
            policy.addEqualsCondition("Content-Type", contentType);
            policy.addContentLengthRangeCondition(bytes, bytes);
            Map<String, String> minioFormData = minioClient.getPresignedPostFormData(policy);
            Map<String, String> completeFormData = new HashMap<>();
            completeFormData.put("key", md5);
            completeFormData.put("Content-Type", contentType);
            completeFormData.putAll(minioFormData);
            String url = property.getEndpoint() + "/" + property.getBucketName();
            long expireAt = System.currentTimeMillis() + minutes * 60_000L;
            return new PresignedPostForm(url, completeFormData, md5, expireAt);
        } catch (Exception e) {
            throw new RuntimeException("Generate presigned post failed", e);
        }
    }

    public record PresignedPostForm(String url, Map<String, String> formData, String objectKey, long expireAt) {
    }
}
