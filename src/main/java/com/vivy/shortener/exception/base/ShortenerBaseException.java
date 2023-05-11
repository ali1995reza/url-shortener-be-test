package com.vivy.shortener.exception.base;

public class ShortenerBaseException extends RuntimeException {

    private final ExceptionCode code;

    public ShortenerBaseException(ExceptionCode code) {
        this.code = code;
    }

    public ShortenerBaseException(String message, ExceptionCode code) {
        super(message);
        this.code = code;
    }

    public ShortenerBaseException(String message, Throwable cause, ExceptionCode code) {
        super(message, cause);
        this.code = code;
    }

    public ShortenerBaseException(Throwable cause, ExceptionCode code) {
        super(cause);
        this.code = code;
    }

    public ShortenerBaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ExceptionCode code) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    public ExceptionCode getCode() {
        return code;
    }
}
