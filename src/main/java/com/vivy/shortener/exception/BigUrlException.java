package com.vivy.shortener.exception;

import com.vivy.shortener.exception.base.ExceptionCodes;
import com.vivy.shortener.exception.base.ShortenerBaseException;

public class BigUrlException extends ShortenerBaseException {

    public BigUrlException() {
        super(ExceptionCodes.BIG_URL);
    }

    public BigUrlException(String message) {
        super(message, ExceptionCodes.BIG_URL);
    }

    public BigUrlException(String message, Throwable cause) {
        super(message, cause, ExceptionCodes.BIG_URL);
    }

    public BigUrlException(Throwable cause) {
        super(cause, ExceptionCodes.BIG_URL);
    }

    public BigUrlException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace, ExceptionCodes.BIG_URL);
    }
}
