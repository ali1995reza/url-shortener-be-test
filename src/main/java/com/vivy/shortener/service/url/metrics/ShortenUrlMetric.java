package com.vivy.shortener.service.url.metrics;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import org.springframework.stereotype.Component;

@Component
public class ShortenUrlMetric {

    private final Counter shortenUrlTryCounter;
    private final Counter shortenUrlSuccessCounter;
    private final Counter shortenUrlFailCounter;

    public ShortenUrlMetric(CollectorRegistry registry) {
        this.shortenUrlTryCounter = Counter
                .build()
                .name("shorten_url_try")
                .help("Number of total times that try to create shorten url")
                .register(registry);
        this.shortenUrlSuccessCounter = Counter
                .build()
                .name("shorten_url_success")
                .help("Number of total times that success to shorten url")
                .register(registry);
        this.shortenUrlFailCounter = Counter
                .build()
                .name("shorten_url_fail")
                .help("Number of total times that fail to shorten url")
                .register(registry);
    }

    public void captureShortenUrlSuccess() {
        this.shortenUrlTryCounter.inc();
        this.shortenUrlSuccessCounter.inc();
    }

    public void captureShortenUrlFail() {
        this.shortenUrlTryCounter.inc();
        this.shortenUrlFailCounter.inc();
    }
}
