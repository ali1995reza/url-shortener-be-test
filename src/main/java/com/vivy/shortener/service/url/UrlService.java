package com.vivy.shortener.service.url;


import com.vivy.shortener.config.ShortenerApplicationConfigurations;
import com.vivy.shortener.exception.BigUrlException;
import com.vivy.shortener.exception.InvalidUrlException;
import com.vivy.shortener.exception.UrlNotFoundException;
import com.vivy.shortener.model.UrlEntity;
import com.vivy.shortener.repository.url.UrlRepository;
import com.vivy.shortener.service.url.metrics.FetchUrlMetric;
import com.vivy.shortener.service.url.metrics.ShortenUrlMetric;
import com.vivy.shortener.util.AssertUtil;
import com.vivy.shortener.util.RandomUtil;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;

@Service
@Slf4j
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlCache urlCache;
    private final Scheduler scheduler;
    private final int urlIdLength;
    private final int repositoryRetryOnSave;
    private final FetchUrlMetric fetchUrlMetric;
    private final ShortenUrlMetric shortenUrlMetric;


    public UrlService(UrlRepository urlRepository, UrlCache urlCache, ShortenerApplicationConfigurations configurations, FetchUrlMetric fetchUrlMetric, ShortenUrlMetric shortenUrlMetric) {
        this.urlRepository = urlRepository;
        this.urlCache = urlCache;
        this.fetchUrlMetric = fetchUrlMetric;
        this.shortenUrlMetric = shortenUrlMetric;
        int reactorThreadPoolSize = configurations.getUrlRepository().getReactorThreadPoolSize();
        AssertUtil.assertIsPositive(reactorThreadPoolSize, "reactor thread pool size should be positive value");
        this.scheduler = Schedulers.newParallel("jdbc-reactor-pool", reactorThreadPoolSize);
        log.info("start jdbc reactor thread pool with size {}", reactorThreadPoolSize);
        this.urlIdLength = configurations.getUrlRepository().getUrlIdLength();
        AssertUtil.assertIsValid(this.urlIdLength, len -> len > 0 && len <= 20, "url id length should be in range [0 , 20]");
        log.info("len of url-id will be {}", this.urlIdLength);
        this.repositoryRetryOnSave = configurations.getUrlRepository().getRetryOnSave();
        AssertUtil.assertIsNotNegative(this.repositoryRetryOnSave, "retry on save config should not be negative value");
        log.info("in case of error while inserting in database system will retry {} times", this.repositoryRetryOnSave);
    }

    public Mono<String> saveUrl(String url) {
        if (log.isDebugEnabled()) {
            log.debug("requested to create short url for [{}]", url);
        }
        return Mono.fromSupplier(() -> validateOriginalUrl(url))
                .map(originalUrl -> urlRepository.save(
                        UrlEntity.builder()
                                .urlId(RandomUtil.randomAlphaNumericString(urlIdLength))
                                .originalUrl(originalUrl)
                                .build()
                )).subscribeOn(scheduler)
                .retry(repositoryRetryOnSave)
                .doOnSuccess(urlEntity -> handleOnSuccessSaveUrl(urlEntity.getUrlId(), url))
                .doOnError(this::handleOnErrorSaveUrl)
                .map(UrlEntity::getUrlId);
    }

    public Mono<String> getOriginalUrlByUrlId(String urlId) {
        if (log.isDebugEnabled()) {
            log.debug("requested to get original url for short-url-id [{}]", urlId);
        }
        return urlCache.getUrl(urlId)
                .doOnSuccess(result -> handleOnSuccessCacheFetching(urlId, result))
                .doOnError(this::handleOnErrorCacheFetching)
                .onErrorReturn(Optional.empty())
                .flatMap(url -> {
                    if (url.isPresent()) {
                        return Mono.just(url);
                    } else {
                        return getOriginalUrlByUrlIdFromRepositoryAndPutInCache(urlId);
                    }
                })
                .map(url -> url.orElseThrow(() -> new UrlNotFoundException().withUrlId(urlId)));
    }

    public Mono<Boolean> deleteUrlByUrlId(String urlId) {
        return Mono.fromSupplier(() -> urlRepository.deleteByUrlId(urlId))
                .subscribeOn(scheduler)
                .flatMap(deletedCount -> {
                    if (deletedCount > 0) {
                        return urlCache.deleteUrl(urlId).onErrorReturn(err -> {
                            if (log.isWarnEnabled()) {
                                log.warn("error while removing original url of short-url [{}] from cache", urlId);
                            }
                            return true;
                        }, false).thenReturn(true);
                    }
                    return Mono.just(false);
                });
    }

    private String validateOriginalUrl(String originalUrl) {
        AssertUtil.assertValidUrl(originalUrl, () -> new InvalidUrlException("Url is not valid"));
        AssertUtil.assertIsValid(originalUrl.length(), len -> len <= 4096, () -> new BigUrlException("Url is too big"));
        return originalUrl;
    }

    private Mono<Optional<String>> getOriginalUrlByUrlIdFromRepositoryAndPutInCache(String urlId) {
        return Mono.fromSupplier(() -> urlRepository.findByUrlId(urlId))
                .subscribeOn(scheduler)
                .doOnSuccess(result -> handleOnSuccessDatabaseFetching(urlId, result))
                .doOnError(this::handleOnErrorDatabaseFetching)
                .map(o -> o.map(UrlEntity::getOriginalUrl))
                .flatMap(originalUrl -> saveInCache(urlId, originalUrl));
    }

    private Mono<Optional<String>> saveInCache(String urlId, Optional<String> originalUrl) {
        if (originalUrl.isEmpty()) {
            return Mono.empty();
        }
        return urlCache.saveUrl(urlId, originalUrl.get())
                .onErrorReturn(err -> {
                    if (log.isWarnEnabled()) {
                        log.warn("error while putting original url of short-url [{}] into cache", urlId);
                    }
                    return true;
                }, false)
                .thenReturn(originalUrl);
    }

    private void handleOnSuccessDatabaseFetching(String urlId, Optional<UrlEntity> url) {
        if (log.isDebugEnabled()) {
            if (url.isPresent()) {
                log.debug("url fetched from database successfully, url-id is {}", urlId);
            } else {
                log.debug("url fetched from database failed, url-id is {}", urlId);
            }
        }
        if (url.isPresent()) {
            fetchUrlMetric.captureDatabaseFetchingSuccess();
        } else {
            fetchUrlMetric.captureDatabaseFetchingNotFound();
        }
    }

    private void handleOnErrorDatabaseFetching(Throwable e) {
        if (log.isErrorEnabled()) {
            log.error("error while fetching url from database", e);
        }
        fetchUrlMetric.captureDatabaseFetchingFail();
    }

    private void handleOnSuccessCacheFetching(String urlId, Optional<String> url) {
        if (log.isDebugEnabled()) {
            if (url.isPresent()) {
                log.debug("url fetched from cache successfully, url-id is {}", urlId);
            } else {
                log.debug("url fetched from database failed, url-id is {}", urlId);
            }
        }
        if (url.isPresent()) {
            fetchUrlMetric.captureCacheFetchingSuccess();
        } else {
            fetchUrlMetric.captureCacheFetchingNotFound();
        }
    }

    private void handleOnErrorCacheFetching(Throwable e) {
        if (log.isErrorEnabled()) {
            log.error("error while fetching url from cache", e);
        }
        fetchUrlMetric.captureCacheFetchingFail();
    }

    private void handleOnSuccessSaveUrl(String urlId, String originalUrl) {
        if (log.isDebugEnabled()) {
            log.debug("url fetched from cache successfully, url-id is {} and original-url is {}", urlId, originalUrl);
        }
        shortenUrlMetric.captureShortenUrlSuccess();
    }

    private void handleOnErrorSaveUrl(Throwable e) {
        if (log.isErrorEnabled()) {
            log.error("error while save url", e);
        }
        shortenUrlMetric.captureShortenUrlFail();
    }

    @PreDestroy
    private void destroy() {
        log.info("closing reactive scheduler");
        scheduler.dispose();
        log.info("reactive scheduler closed successfully");
    }

}
