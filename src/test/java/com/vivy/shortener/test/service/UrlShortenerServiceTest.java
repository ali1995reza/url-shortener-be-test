package com.vivy.shortener.test.service;

import com.vivy.shortener.UrlShortenerApplication;
import com.vivy.shortener.service.shortener.UrlShortenerService;
import com.vivy.shortener.test.base.BaseTest;
import com.vivy.shortener.util.UrlUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import static com.vivy.shortener.constants.SpringConfigNameConstants.*;
import static com.vivy.shortener.test.service.TestData.TEST_ORIGINAL_URL;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {UrlShortenerApplication.class, ObserverUrlRepository.class})
public class UrlShortenerServiceTest extends BaseTest {

    @Autowired
    private UrlShortenerService urlShortenerService;
    @Autowired
    private Environment environment;

    private String baseUrl;

    private synchronized String getBaseUrl() {
        if (baseUrl != null) {
            return baseUrl;
        }
        String host = environment.getProperty(SERVER_HOST_CONFIG_NAME, String.class, "localhost");
        int port = environment.getProperty(SERVER_PORT_CONFIG_NAME, Integer.class, 8080);
        boolean secure = environment.getProperty(SERVER_SSL_ENABLED_CONFIG_NAME, Boolean.class, false);

        baseUrl = UrlUtil.createBaseUrl(host, port, secure);

        return baseUrl;
    }

    @Test
    public void testCreateShortUrl() {

        String shortUrl = urlShortenerService.createShortUrl(TEST_ORIGINAL_URL).block();

        assertThat(shortUrl).matches("(" + getBaseUrl() + ")/[a-z0-9]+");

    }

    @Test
    public void testGetOriginalUrlByShortUrl() {

        String shortUrl = urlShortenerService.createShortUrl(TEST_ORIGINAL_URL).block();

        assertThat(urlShortenerService.getOriginalUrlByShortUrl(shortUrl).block()).isEqualTo(TEST_ORIGINAL_URL);

    }

}
