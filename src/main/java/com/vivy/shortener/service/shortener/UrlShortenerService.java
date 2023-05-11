package com.vivy.shortener.service.shortener;

import com.vivy.shortener.exception.InvalidUrlException;
import com.vivy.shortener.service.url.UrlService;
import com.vivy.shortener.util.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

import static com.vivy.shortener.constants.SpringConfigNameConstants.*;

@Service
@Slf4j
public class UrlShortenerService {

    private final UrlService urlService;
    private final String baseUrl;
    private final String baseUrlWithSlash;
    private final Pattern urlPattern;


    public UrlShortenerService(UrlService urlService, Environment environment) {
        this.urlService = urlService;
        String host = environment.getProperty(SERVER_HOST_CONFIG_NAME, String.class, "localhost");
        int port = environment.getProperty(SERVER_PORT_CONFIG_NAME, Integer.class, 8080);
        boolean secure = environment.getProperty(SERVER_SSL_ENABLED_CONFIG_NAME, Boolean.class, false);
        this.baseUrl = UrlUtil.createBaseUrl(host, port, secure);
        log.info("base url of application is {}", this.baseUrl);
        this.urlPattern = Pattern.compile("(" + baseUrl + ")/[a-z0-9]+");
        this.baseUrlWithSlash = baseUrl + "/";
    }

    public Mono<String> createShortUrl(String url) {
        if (url != null && url.startsWith(this.baseUrl)) {
            throw new InvalidUrlException("Url can not refer to this website");
        }
        return urlService.saveUrl(url)
                .map(this::getFullUrl);
    }

    public Mono<String> getOriginalUrlByUrlId(String urlId) {
        return urlService.getOriginalUrlByUrlId(urlId);
    }

    public Mono<String> getOriginalUrlByShortUrl(String url) {
        if (!urlPattern.matcher(url).matches()) {
            throw new InvalidUrlException(url);
        }
        String urlId = url.replace(baseUrlWithSlash, "");
        return getOriginalUrlByUrlId(urlId);
    }

    private String getFullUrl(String urlId) {
        return baseUrl + "/" + urlId;
    }


}
