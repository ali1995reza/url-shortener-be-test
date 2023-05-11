package com.vivy.shortener.exception;

import com.vivy.shortener.exception.base.ExceptionCodes;
import com.vivy.shortener.exception.base.ShortenerBaseException;
import lombok.Getter;
import lombok.With;

@With
@Getter
public class UrlNotFoundException extends ShortenerBaseException {

    private String urlId;

    public UrlNotFoundException() {
        super(ExceptionCodes.URL_NOT_FOUND);
    }

    public UrlNotFoundException(String message) {
        super(message, ExceptionCodes.URL_NOT_FOUND);
    }

    public UrlNotFoundException(String message, Throwable cause) {
        super(message, cause, ExceptionCodes.URL_NOT_FOUND);
    }

    public UrlNotFoundException(Throwable cause) {
        super(cause, ExceptionCodes.URL_NOT_FOUND);
    }

    public UrlNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace, ExceptionCodes.URL_NOT_FOUND);
    }
}
