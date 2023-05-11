package com.vivy.shortener.service.url.metrics;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import org.springframework.stereotype.Component;

@Component
public class FetchUrlMetric {

    private final Counter databaseFetchUrlTryCounter;
    private final Counter databaseFetchUrlSuccessCounter;
    private final Counter databaseFetchUrlFailCounter;
    private final Counter databaseFetchUrlNotFoundCounter;

    private final Counter cacheFetchUrlTryCounter;
    private final Counter cacheFetchUrlSuccessCounter;
    private final Counter cacheFetchUrlFailCounter;
    private final Counter cacheFetchUrlNotFoundCounter;


    public FetchUrlMetric(CollectorRegistry registry) {
        this.databaseFetchUrlTryCounter = Counter
                .build()
                .name("database_fetch_url_try")
                .help("Number of total times that try to fetch original url from database")
                .register(registry);
        this.databaseFetchUrlSuccessCounter = Counter
                .build()
                .name("database_fetch_url_success")
                .help("Number of total times that success to fetch original url from database")
                .register(registry);
        this.databaseFetchUrlFailCounter = Counter
                .build()
                .name("database_fetch_url_fail")
                .help("Number of total times that fail to fetch original url from database")
                .register(registry);
        this.databaseFetchUrlNotFoundCounter = Counter
                .build()
                .name("database_fetch_url_not_found")
                .help("Number of total times that not found original url in database")
                .register(registry);


        this.cacheFetchUrlTryCounter = Counter
                .build()
                .name("cache_fetch_url_try")
                .help("Number of total times that try to fetch original url from cache")
                .register(registry);
        this.cacheFetchUrlSuccessCounter = Counter
                .build()
                .name("cache_fetch_url_success")
                .help("Number of total times that success to fetch original url from cache")
                .register(registry);
        this.cacheFetchUrlFailCounter = Counter
                .build()
                .name("cache_fetch_url_fail")
                .help("Number of total times that fail to fetch original url from cacge")
                .register(registry);
        this.cacheFetchUrlNotFoundCounter = Counter
                .build()
                .name("cache_fetch_url_not_found")
                .help("Number of total times that not found original url in cache")
                .register(registry);
    }

    public void captureDatabaseFetchingSuccess() {
        databaseFetchUrlTryCounter.inc();
        databaseFetchUrlSuccessCounter.inc();
    }

    public void captureDatabaseFetchingFail() {
        databaseFetchUrlTryCounter.inc();
        databaseFetchUrlFailCounter.inc();
    }

    public void captureDatabaseFetchingNotFound() {
        databaseFetchUrlTryCounter.inc();
        databaseFetchUrlNotFoundCounter.inc();
    }


    public void captureCacheFetchingSuccess() {
        cacheFetchUrlTryCounter.inc();
        cacheFetchUrlSuccessCounter.inc();
    }

    public void captureCacheFetchingFail() {
        cacheFetchUrlTryCounter.inc();
        cacheFetchUrlFailCounter.inc();
    }

    public void captureCacheFetchingNotFound() {
        cacheFetchUrlTryCounter.inc();
        cacheFetchUrlNotFoundCounter.inc();
    }

}
