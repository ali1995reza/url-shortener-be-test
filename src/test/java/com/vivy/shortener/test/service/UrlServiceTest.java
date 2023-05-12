package com.vivy.shortener.test.service;

import com.vivy.shortener.exception.BigUrlException;
import com.vivy.shortener.exception.InvalidUrlException;
import com.vivy.shortener.exception.UrlNotFoundException;
import com.vivy.shortener.service.url.UrlCache;
import com.vivy.shortener.service.url.UrlService;
import com.vivy.shortener.service.url.metrics.FetchUrlMetric;
import com.vivy.shortener.service.url.metrics.ShortenUrlMetric;
import com.vivy.shortener.test.base.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static com.vivy.shortener.test.service.TestData.TEST_ORIGINAL_URL;
import static com.vivy.shortener.test.util.TestUtil.createBigString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
public class UrlServiceTest extends BaseTest {

    @Autowired
    private UrlService urlService;
    @MockBean
    private UrlCache mockCache;
    @Autowired
    private ObserverUrlRepository urlRepository;
    @MockBean
    private FetchUrlMetric fetchUrlMetric;
    @MockBean
    private ShortenUrlMetric shortenUrlMetric;

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
    public void testGetOriginalUrlByNotExistsUrlId() {
        Mockito.when(mockCache.getUrl(any())).thenReturn(Mono.just(Optional.empty()));
        assertThrows(UrlNotFoundException.class, () -> urlService.getOriginalUrlByUrlId("1234abc").block());
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
        assertThrows(InvalidUrlException.class, () -> urlService.saveUrl("invalid_url").block());
    }

    @Test
    public void testBigOriginalUrlThrowsException() {
        assertThrows(BigUrlException.class, () -> {
            urlService.saveUrl("https://www.google.com?q=" + createBigString('s', 4096)).block();
        });
    }


}
