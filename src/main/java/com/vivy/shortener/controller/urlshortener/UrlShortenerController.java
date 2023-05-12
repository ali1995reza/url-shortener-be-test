package com.vivy.shortener.controller.urlshortener;

import com.vivy.shortener.controller.urlshortener.dto.CreateShortUrlResponseDto;
import com.vivy.shortener.controller.urlshortener.dto.GetOriginalUrlResponseDto;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "create short-url from provided url")
    @GetMapping(value = "/create-short-url", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<CreateShortUrlResponseDto> createShortUrl(@RequestParam("url") String url) {
        return shortUrlControllerService.createShortUrl(url);
    }

    @Operation(summary = "get original-url backed of the provided short-url")
    @GetMapping(value = "/get-original-url", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<GetOriginalUrlResponseDto> getOriginalUrl(@RequestParam("short_url") String shortUrl) {
        return shortUrlControllerService.getOriginalUrl(shortUrl);
    }

    @Operation(summary = "redirect to original-url")
    @GetMapping(value = "/{url_id:^(?!favicon\\.ico$)^(?!swagger$)[^\\/].+$}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Void>> openUrl(@PathVariable("url_id") String urlId) {
        return shortUrlControllerService.openUrl(urlId);
    }
}
