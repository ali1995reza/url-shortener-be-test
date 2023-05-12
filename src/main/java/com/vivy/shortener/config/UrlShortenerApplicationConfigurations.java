package com.vivy.shortener.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "url-shortener-app", ignoreUnknownFields = false)
@Data
@NoArgsConstructor
public class UrlShortenerApplicationConfigurations {

    private UrlRepository urlRepository;
    private UrlCache urlCache;

    @Data
    @NoArgsConstructor
    public static class UrlRepository {

        private int retryOnSave;
        private int reactorThreadPoolSize;
        private int urlIdLength;

    }

    @Data
    @NoArgsConstructor
    public static class UrlCache {

        private String type;
        private Properties properties;


        @Data
        @NoArgsConstructor
        public static class Properties {

            private int inMemorySize;
            private Duration inMemoryMaxValidDuration;
            private String redisAddress;
            private String redisPassword;
            private int redisConnectionPoolSize;
        }
    }
}
