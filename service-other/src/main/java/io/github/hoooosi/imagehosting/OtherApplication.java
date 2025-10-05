package io.github.hoooosi.imagehosting;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableCaching
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableDiscoveryClient
@MapperScan("io.github.hoooosi.imagehosting.mapper")
public class OtherApplication {
    public static void main(String[] args) {
        SpringApplication.run(OtherApplication.class, args);
    }
}
