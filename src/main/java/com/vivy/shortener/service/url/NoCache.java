package com.vivy.shortener.service.url;

import reactor.core.publisher.Mono;

import java.util.Optional;

public class NoCache implements UrlCache {

    private final static NoCache INSTANCE  = new NoCache();

    public static NoCache getInstance() {
        return INSTANCE;
    }

    private NoCache() {

    }

    @Override
    public Mono<Boolean> saveUrl(String urlId, String originalId) {
        return Mono.just(true);
    }

    @Override
    public Mono<Boolean> deleteUrl(String urlId) {
        return Mono.just(true);
    }

    @Override
    public Mono<Optional<String>> getUrl(String urlId) {
        return Mono.just(Optional.empty());
    }
}
