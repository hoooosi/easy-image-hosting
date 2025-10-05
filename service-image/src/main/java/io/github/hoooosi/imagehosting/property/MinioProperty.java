package io.github.hoooosi.imagehosting.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperty {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
}
