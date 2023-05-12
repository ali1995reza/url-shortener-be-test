package com.vivy.shortener.test.base;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
        "LOGSTASH_LOG_FILE=disable"
})
public class BaseTest {
}
