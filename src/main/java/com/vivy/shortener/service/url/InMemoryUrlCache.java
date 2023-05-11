package com.vivy.shortener.service.url;

import com.google.common.cache.Cache;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class InMemoryUrlCache implements UrlCache {

    private final Cache<String, String> cache;

    public InMemoryUrlCache(Cache<String, String> cache) {
        this.cache = cache;
    }

    @Override
    public Mono<Boolean> saveUrl(String urlId, String originalId) {
        return Mono.fromSupplier(() -> {
            cache.put(urlId, originalId);
            return true;
        });
    }

    @Override
    public Mono<Boolean> deleteUrl(String urlId) {
        return Mono.fromSupplier(() -> {
            cache.invalidate(urlId);
            return true;
        });
    }

    @Override
    public Mono<Optional<String>> getUrl(String urlId) {
        return Mono.fromSupplier(() -> Optional.ofNullable(cache.getIfPresent(urlId)));
    }
}
