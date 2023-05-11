package com.vivy.shortener.exception;

import com.vivy.shortener.exception.base.ExceptionCodes;
import com.vivy.shortener.exception.base.ShortenerBaseException;

public class InvalidUrlException extends ShortenerBaseException {

    public InvalidUrlException() {
        super(ExceptionCodes.INVALID_URL);
    }

    public InvalidUrlException(String message) {
        super(message, ExceptionCodes.INVALID_URL);
    }

    public InvalidUrlException(String message, Throwable cause) {
        super(message, cause, ExceptionCodes.INVALID_URL);
    }

    public InvalidUrlException(Throwable cause) {
        super(cause, ExceptionCodes.INVALID_URL);
    }

    public InvalidUrlException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace, ExceptionCodes.INVALID_URL);
    }
}
