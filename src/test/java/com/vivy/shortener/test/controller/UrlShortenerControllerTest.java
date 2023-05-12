package com.vivy.shortener.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivy.shortener.UrlShortenerApplication;
import com.vivy.shortener.controller.urlshortener.dto.CreateShortUrlResponseDto;
import com.vivy.shortener.exception.base.ExceptionCodes;
import com.vivy.shortener.test.base.BaseTest;
import com.vivy.shortener.test.service.TestData;
import com.vivy.shortener.util.UrlUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.vivy.shortener.constants.SpringConfigNameConstants.*;
import static com.vivy.shortener.test.util.TestUtil.createBigString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {UrlShortenerApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UrlShortenerControllerTest extends BaseTest {

    private final static String CREATE_SHORT_URL_PATH = "/create-short-url";
    private final static String GET_ORIGINAL_URL_PATH = "/get-original-url";


    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private Environment environment;
    @Autowired
    private MockMvc mockMvc;

    private String baseUrl;


    @Test
    void testCrateShorUrl() throws Exception {
        performCreateShorUrlRequest(TestData.TEST_ORIGINAL_URL)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.errorCode").doesNotHaveJsonPath())
                .andExpect(jsonPath("$.errorMessage").doesNotHaveJsonPath())
                .andExpect(jsonPath("$.shortUrl").isString())
                .andReturn();
    }

    @Test
    void testCreateShortUrlWithInvalidUrl() throws Exception {

        performCreateShorUrlRequest("invalid_url")
                .andExpect(status().is(ExceptionCodes.INVALID_URL.httpStatus().value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value(ExceptionCodes.INVALID_URL.errorCode()))
                .andExpect(jsonPath("$.errorMessage").isString())
                .andExpect(jsonPath("$.shortUrl").doesNotHaveJsonPath())
                .andReturn();
    }

    @Test
    void testCreateShortUrlWithBigUrl() throws Exception {

        performCreateShorUrlRequest("https://www.google.com?q=" + createBigString('s', 4096))
                .andExpect(status().is(ExceptionCodes.BIG_URL.httpStatus().value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value(ExceptionCodes.BIG_URL.errorCode()))
                .andExpect(jsonPath("$.errorMessage").isString())
                .andExpect(jsonPath("$.shortUrl").doesNotHaveJsonPath())
                .andReturn();
    }

    @Test
    void testGetOriginalUrl() throws Exception {
        String content = performCreateShorUrlRequest(TestData.TEST_ORIGINAL_URL)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.errorCode").doesNotHaveJsonPath())
                .andExpect(jsonPath("$.errorMessage").doesNotHaveJsonPath())
                .andExpect(jsonPath("$.shortUrl").isString())
                .andReturn()
                .getResponse()
                .getContentAsString();
        CreateShortUrlResponseDto response = mapper.readValue(content, CreateShortUrlResponseDto.class);
        performGetOriginalUrl(response.getShortUrl())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.errorCode").doesNotHaveJsonPath())
                .andExpect(jsonPath("$.errorMessage").doesNotHaveJsonPath())
                .andExpect(jsonPath("$.originalUrl").value(TestData.TEST_ORIGINAL_URL));
    }

    @Test
    void testGetOriginalUrlFromInvalidUrl() throws Exception {
        performGetOriginalUrl("invalid_url")
                .andExpect(status().is(ExceptionCodes.INVALID_URL.httpStatus().value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value(ExceptionCodes.INVALID_URL.errorCode()))
                .andExpect(jsonPath("$.errorMessage").isString())
                .andExpect(jsonPath("$.shortUrl").doesNotHaveJsonPath())
                .andReturn();
    }


    @Test
    void testGetOriginalUrlFromNotExistsUrl() throws Exception {
        performGetOriginalUrl(getBaseUrl() + "/" + "abc1234")
                .andExpect(status().is(ExceptionCodes.URL_NOT_FOUND.httpStatus().value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value(ExceptionCodes.URL_NOT_FOUND.errorCode()))
                .andExpect(jsonPath("$.errorMessage").isString())
                .andExpect(jsonPath("$.shortUrl").doesNotHaveJsonPath())
                .andReturn();
    }

    @Test
    void testOpenShortUrl() throws Exception {
        String content = performCreateShorUrlRequest(TestData.TEST_ORIGINAL_URL)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.errorCode").doesNotHaveJsonPath())
                .andExpect(jsonPath("$.errorMessage").doesNotHaveJsonPath())
                .andExpect(jsonPath("$.shortUrl").isString())
                .andReturn()
                .getResponse()
                .getContentAsString();
        CreateShortUrlResponseDto response = mapper.readValue(content, CreateShortUrlResponseDto.class);
        performOpenShortUrl(response.getShortUrl())
                .andExpect(status().isPermanentRedirect())
                .andExpect(redirectedUrl(TestData.TEST_ORIGINAL_URL));
    }

    @Test
    void testOpenNotExistsShortUrl() throws Exception {
        performOpenShortUrl(getBaseUrl() + "/123abc")
                .andExpect(status().is(ExceptionCodes.URL_NOT_FOUND.httpStatus().value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value(ExceptionCodes.URL_NOT_FOUND.errorCode()))
                .andExpect(jsonPath("$.errorMessage").isString())
                .andExpect(jsonPath("$.shortUrl").doesNotHaveJsonPath())
                .andReturn();
    }

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

    private ResultActions performRequest(MockHttpServletRequestBuilder request) throws Exception {
        MvcResult result = mockMvc.perform(request)
                .andExpect(request().asyncStarted())
                .andExpect(request().asyncResult(notNullValue()))
                .andReturn();
        return mockMvc.perform(asyncDispatch(result)).andDo(print());
    }

    private ResultActions performCreateShorUrlRequest(String originalUrl) throws Exception {
        return performRequest(get(CREATE_SHORT_URL_PATH)
                .queryParam("url", originalUrl));
    }

    private ResultActions performGetOriginalUrl(String shortUrl) throws Exception {
        return performRequest(get(GET_ORIGINAL_URL_PATH)
                .queryParam("short_url", shortUrl));
    }

    private ResultActions performOpenShortUrl(String shortUrl) throws Exception {
        return performRequest(get(shortUrl));
    }

}
