package com.vivy.shortener.service.url;


import reactor.core.publisher.Mono;

import java.util.Optional;

public interface UrlCache {

    Mono<Boolean> saveUrl(String urlId, String originalId);

    Mono<Boolean> deleteUrl(String urlId);

    Mono<Optional<String>> getUrl(String urlId);

}
