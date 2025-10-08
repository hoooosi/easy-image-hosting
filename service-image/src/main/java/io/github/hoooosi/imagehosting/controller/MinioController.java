package io.github.hoooosi.imagehosting.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.hoooosi.imagehosting.constant.TopicNames;
import io.github.hoooosi.imagehosting.manager.ImageManager;
import io.github.hoooosi.imagehosting.property.MinioProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.web.bind.annotation.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/minio")
@Slf4j
@AllArgsConstructor
@Tag(name = "MinIO Webhook I/F")
public class MinioController {
    private final ImageManager imageManager;
    private final RocketMQTemplate rocketMQTemplate;
    private final MinioProperty minioProperty;


    @RequestMapping("/webhook")
    @Operation(summary = "WEBHOOK")
    public void handleMinioEvent(@RequestBody MinioWebhookRequest request) {
        log.info("Receive event notifications from minio: EventName={}, Key={}, RecordsCount={}",
                request.getEventName(), request.getKey(), request.getRecords().size());
        for (MinioEventRecord record : request.getRecords()) {
            try {
                // Only process object creation events
                String eventName = record.getEventName();
                if (!eventName.startsWith("s3:ObjectCreated:")) {
                    log.info("Ignore non-creation events: {}", eventName);
                    continue;
                }

                String objectKey = record.getS3().getObject().getKey();
                if (objectKey.startsWith("thumbnail")) {
                    log.info("Ignore thumbnail object: {}", objectKey);
                    continue;
                }

                log.info("Uploaded object key: {}}", objectKey);
                rocketMQTemplate.convertAndSend(TopicNames.UPLOAD_TOPIC + ":success", objectKey);
            } catch (Exception e) {
                log.error("Failed to process MinIO events: {}", record.getEventName(), e);
            }
        }
    }
}

@Data
class MinioWebhookRequest implements Serializable {
    @JsonProperty("EventName")
    private String eventName;

    @JsonProperty("Key")
    private String key;

    @JsonProperty("Records")
    private List<MinioEventRecord> records;
}

@Data
class MinioEventRecord implements Serializable {
    private String eventVersion;
    private String eventSource;
    private String awsRegion;
    private String eventTime;
    private String eventName;
    private UserIdentity userIdentity;
    private Map<String, Object> requestParameters;
    private Map<String, String> responseElements;
    private S3Event s3;
    private SourceInfo source;
}


@Data
class UserIdentity implements Serializable {
    private String principalId;
}


@Data
class S3Event implements Serializable {
    private String s3SchemaVersion;
    private String configurationId;
    private BucketInfo bucket;
    private ObjectInfo object;
}

@Data
class BucketInfo implements Serializable {
    private String name;
    private UserIdentity ownerIdentity;
    private String arn;
}

@Data
class ObjectInfo implements Serializable {
    private String key;
    private long size;
    private String eTag;
    private String contentType;
    private Map<String, String> userMetadata;
    private String sequencer;
}

@Data
class SourceInfo implements Serializable {
    private String host;
    private String port;
    private String userAgent;
}