package com.vivy.shortener.exception.base;

import org.springframework.http.HttpStatus;

public enum ExceptionCodes implements ExceptionCode {
    URL_NOT_FOUND(HttpStatus.NOT_FOUND, 10001),
    INVALID_URL(HttpStatus.BAD_REQUEST, 10002),
    BIG_URL(HttpStatus.BAD_REQUEST, 10003)
    ;

    private final HttpStatus httpStatus;
    private final int errorCode;

    ExceptionCodes(HttpStatus httpStatus, int errorCode) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }

    @Override
    public int errorCode() {
        return errorCode;
    }
}
