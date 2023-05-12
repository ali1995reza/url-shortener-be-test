package com.vivy.shortener.controller.urlshortener;

import com.vivy.shortener.controller.urlshortener.dto.CreateShortUrlResponseDto;
import com.vivy.shortener.controller.urlshortener.dto.GetOriginalUrlResponseDto;
import com.vivy.shortener.service.shortener.UrlShortenerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
@AllArgsConstructor
class UrlShortenerControllerService {

    private final UrlShortenerService urlShortenerService;


    public Mono<CreateShortUrlResponseDto> createShortUrl(String url) {
        return urlShortenerService.createShortUrl(url)
                .map(shortUrl -> CreateShortUrlResponseDto.builder()
                        .success(true)
                        .shortUrl(shortUrl)
                        .build());
    }

    public Mono<GetOriginalUrlResponseDto> getOriginalUrl(String shortUrl) {
        return urlShortenerService.getOriginalUrlByShortUrl(shortUrl)
                .map(originalUrl -> GetOriginalUrlResponseDto.builder()
                        .success(true)
                        .originalUrl(originalUrl)
                        .build());
    }

    public Mono<ResponseEntity<Void>> openUrl(String urlId) {
        return urlShortenerService.getOriginalUrlByUrlId(urlId)
                .map(url ->
                        ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
                                .location(URI.create(url))
                                .build()
                );
    }
}
