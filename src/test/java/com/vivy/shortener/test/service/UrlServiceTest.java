package com.vivy.shortener.test.service;

import com.vivy.shortener.ShortenerApplication;
import com.vivy.shortener.exception.BigUrlException;
import com.vivy.shortener.exception.InvalidUrlException;
import com.vivy.shortener.service.url.UrlCache;
import com.vivy.shortener.service.url.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(classes = {ShortenerApplication.class, ObserverUrlRepository.class})
public class UrlServiceTest {

    private final static String TEST_ORIGINAL_URL = "https://www.google.com";

    @Autowired
    private UrlService urlService;
    @MockBean
    private UrlCache mockCache;
    @Autowired
    private ObserverUrlRepository urlRepository;

    @BeforeEach
    public void beforeEachTest() {
        urlRepository.refresh();
    }

    @Test
    public void testSaveUrl() {

        Mockito.when(mockCache.saveUrl(any(), any())).thenReturn(Mono.just(true));
        Mockito.when(mockCache.getUrl(any())).thenReturn(Mono.just(Optional.empty()));

        String urlId = urlService.saveUrl(TEST_ORIGINAL_URL).block();
        assertThat(urlService.getOriginalUrlByUrlId(urlId).block()).isEqualTo(TEST_ORIGINAL_URL);
        assertThat(urlRepository.getTotalFindByUrlIdCall()).isOne();
    }

    @Test
    public void testGetUrlEvenIfPuttingInCacheMakeException() {

        Mockito.when(mockCache.saveUrl(any(), any())).thenReturn(Mono.error(new RuntimeException("error while saving url in cache")));
        Mockito.when(mockCache.getUrl(any())).thenReturn(Mono.just(Optional.empty()));

        String urlId = urlService.saveUrl(TEST_ORIGINAL_URL).block();

        assertThat(urlService.getOriginalUrlByUrlId(urlId).block()).isEqualTo(TEST_ORIGINAL_URL);

    }

    @Test
    public void testUrlCache() {

        Mockito.when(mockCache.saveUrl(any(), any())).thenReturn(Mono.just(true));

        String urlId = urlService.saveUrl(TEST_ORIGINAL_URL).block();

        Mockito.when(mockCache.getUrl(eq(urlId))).thenReturn(Mono.just(Optional.of(TEST_ORIGINAL_URL)));

        assertThat(urlService.getOriginalUrlByUrlId(urlId).block()).isEqualTo(TEST_ORIGINAL_URL);

        assertThat(urlRepository.getTotalFindByUrlIdCall()).isZero();

    }

    @Test
    public void testUrlWillFetchFromDatabaseEvenIfCacheFailOnFetch() {

        Mockito.when(mockCache.saveUrl(any(), any())).thenReturn(Mono.just(true));

        String urlId = urlService.saveUrl(TEST_ORIGINAL_URL).block();

        Mockito.when(mockCache.getUrl(eq(urlId))).thenReturn(Mono.error(new RuntimeException("some error while fetch url")));

        assertThat(urlService.getOriginalUrlByUrlId(urlId).block()).isEqualTo(TEST_ORIGINAL_URL);

        assertThat(urlRepository.getTotalFindByUrlIdCall()).isOne();

    }

    @Test
    public void testInvalidOriginalUrlThrowsException() {
        assertThrows(InvalidUrlException.class, () -> {
            urlService.saveUrl("invalid_url");
        });
    }

    @Test
    public void testBigOriginalUrlThrowsException() {
        assertThrows(BigUrlException.class, () -> {
            urlService.saveUrl("https://www.google.com?q=" + createBigString('s', 4096));
        });
    }


    private String createBigString(char c, int size) {
        StringBuilder builder = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            builder.append(c);
        }
        return builder.toString();
    }


}
