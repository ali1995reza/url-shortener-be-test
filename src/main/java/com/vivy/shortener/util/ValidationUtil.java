package com.vivy.shortener.util;

import org.apache.commons.validator.routines.UrlValidator;

public class ValidationUtil {

    private final static UrlValidator URL_VALIDATOR = new UrlValidator(new String[]{"http", "https"});

    public static boolean isValidUrl(String url) {
        if(url == null) {
            return false;
        }
        return URL_VALIDATOR.isValid(url);
    }

}
