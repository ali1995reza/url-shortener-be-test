package com.vivy.shortener.service.url;

import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class RedisUrlCache implements UrlCache {

    private final RMapReactive<String, String> map;

    public RedisUrlCache(String mapName, RedissonClient client) {
        this.map = client.reactive().getMap(mapName);
    }

    @Override
    public Mono<Boolean> saveUrl(String urlId, String originalId) {
        return map.put(urlId, originalId).thenReturn(true);
    }

    @Override
    public Mono<Boolean> deleteUrl(String urlId) {
        return map.remove(urlId).thenReturn(true);
    }

    @Override
    public Mono<Optional<String>> getUrl(String urlId) {
        return map.get(urlId).map(Optional::ofNullable);
    }
}
