package io.github.hoooosi.imagehosting.config;

import io.github.hoooosi.imagehosting.property.MinioProperty;
import io.minio.MinioClient;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class MinioConfig {
    private final MinioProperty property;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(property.getEndpoint())
                .credentials(property.getAccessKey(), property.getSecretKey())
                .build();

    }
}
