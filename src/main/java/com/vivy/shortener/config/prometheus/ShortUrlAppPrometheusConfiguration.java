package com.vivy.shortener.config.prometheus;

import io.prometheus.client.CollectorRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShortUrlAppPrometheusConfiguration {

    @Bean
    @ConditionalOnMissingBean
    CollectorRegistry defaultRegistry() {
        return CollectorRegistry.defaultRegistry;
    }
}
