package io.github.hoooosi.imagehosting.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Configuration
public class Config {

    @Bean(name = "ipKeyResolver")
    public KeyResolver ipKeyResolver() {
        return new KeyResolver() {
            @Override
            public Mono<String> resolve(ServerWebExchange exchange) {
                String hostName = Objects.requireNonNull(exchange.getRequest()
                        .getRemoteAddress()).getHostName();
                System.out.println("hostName:" + hostName);
                return Mono.just(hostName);
            }
        };
    }
}
