package com.vivy.shortener.controller.urlshortener;

import com.vivy.shortener.controller.urlshortener.dto.CreateShortUrlResponseDto;
import com.vivy.shortener.controller.urlshortener.dto.GetOriginalUrlResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
public class UrlShortenerController {

    private final UrlShortenerControllerService shortUrlControllerService;

    @GetMapping(value = "/create-short-url", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<CreateShortUrlResponseDto> createShortUrl(@RequestParam("url") String url) {
        return shortUrlControllerService.createShortUrl(url);
    }

    @GetMapping(value = "/get-original-url", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<GetOriginalUrlResponseDto> getOriginalUrl(@RequestParam("short_url") String shortUrl) {
        return shortUrlControllerService.getOriginalUrl(shortUrl);
    }

    @GetMapping(value = "/{url_id:^(?!favicon\\.ico$)[^\\/].+$}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Void>> openUrl(@PathVariable("url_id") String urlId) {
        return shortUrlControllerService.openUrl(urlId);
    }
}
