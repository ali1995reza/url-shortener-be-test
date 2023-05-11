package com.vivy.shortener.config.urlservice;

import com.google.common.cache.CacheBuilder;
import com.vivy.shortener.config.ShortenerApplicationConfigurations;
import com.vivy.shortener.service.url.InMemoryUrlCache;
import com.vivy.shortener.service.url.NoCache;
import com.vivy.shortener.service.url.RedisUrlCache;
import com.vivy.shortener.service.url.UrlCache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@AllArgsConstructor
@Slf4j
public class UrlServiceConfig {

    private enum UrlCacheType {
        DISABLE, IN_MEMORY, REDIS;

        public boolean isDisable() {
            return this == DISABLE;
        }

        public boolean isInMemory() {
            return this == IN_MEMORY;
        }

        public boolean isRedis() {
            return this == REDIS;
        }

        static UrlCacheType findByName(String name) {
            name = name.toUpperCase();
            return switch (name) {
                case "DISABLE" -> DISABLE;
                case "IN_MEMORY" -> IN_MEMORY;
                case "REDIS" -> REDIS;
                default -> throw new RuntimeException("can not found any cache type for [" + name + "]");
            };
        }
    }

    private final ShortenerApplicationConfigurations configurations;


    @Bean
    UrlCache urlCache() {

        UrlCacheType type = UrlCacheType.findByName(configurations.getUrlCache().getType());

        log.info("url cache type is {}", type);

        if (type.isDisable()) {
            log.warn("consider that disable cache may affect application performance and frequency database request");
            return NoCache.getInstance();
        }

        if (type.isInMemory()) {
            int size = configurations.getUrlCache().getProperties().getInMemorySize();
            Duration maxValidDuration = configurations.getUrlCache().getProperties().getInMemoryMaxValidDuration();
            log.info("creating in-memory cache with size {} and max valid duration {}", size, maxValidDuration);
            log.warn("consider in-memory cache make application stateful and horizontal-scaling may be make undefined-behaviors, for this purpose pleases use redis");
            return new InMemoryUrlCache(CacheBuilder.newBuilder()
                    .initialCapacity(size)
                    .maximumSize(size)
                    .build());
        }

        if (type.isRedis()) {
            String redisAddress = configurations.getUrlCache().getProperties().getRedisAddress();
            String redisPassword = configurations.getUrlCache().getProperties().getRedisPassword();
            int connectionPoolSize = configurations.getUrlCache().getProperties().getRedisConnectionPoolSize();

            log.info("creating redis cache - redis address is {}", redisAddress);

            Config config = new Config();
            config.useSingleServer()
                    .setAddress(redisAddress)
                    .setConnectionPoolSize(connectionPoolSize)
                    .setConnectionMinimumIdleSize(connectionPoolSize)
                    .setPassword(redisPassword);

            RedissonClient client = Redisson.create(config);

            return new RedisUrlCache(
                    "url-cache",
                    client
            );
        }

        throw new RuntimeException("can not create url cache");
    }
}
