package com.vivy.shortener.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.vivy")
public class UrlShortenerTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(UrlShortenerTestApplication.class);
    }
}
