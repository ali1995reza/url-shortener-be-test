package com.vivy.shortener.exception.base;

import org.springframework.http.HttpStatus;

public interface ExceptionCode {

    HttpStatus httpStatus();

    int errorCode();

}
