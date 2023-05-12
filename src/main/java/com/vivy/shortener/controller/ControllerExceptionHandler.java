package com.vivy.shortener.controller;

import com.vivy.shortener.controller.dto.BaseResponseDto;
import com.vivy.shortener.exception.base.ShortenerBaseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    private ResponseEntity<BaseResponseDto> handleBaseException(ShortenerBaseException e) {
        return ResponseEntity.status(e.getCode().httpStatus())
                .body(BaseResponseDto.builder()
                        .success(false)
                        .errorCode(e.getCode().errorCode())
                        .errorMessage(e.getMessage())
                        .build());
    }

    public ResponseEntity<BaseResponseDto> handleUnknownException(Throwable e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponseDto.builder()
                        .success(false)
                        .errorCode(-1)
                        .errorMessage("Un unknown exception occurs. Please try again later")
                        .build());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ResponseEntity<BaseResponseDto> handleException(HttpServletRequest request, Throwable e) {
        if (log.isErrorEnabled()) {
            log.error("an exception occurs while handling http request , route {}", request.getPathInfo(), e);
        }
        if (e instanceof ShortenerBaseException baseException) {
            return handleBaseException(baseException);
        }
        return handleUnknownException(e);
    }
}
