package com.honeypot.common.advice;

import com.honeypot.common.model.dto.ErrorResponse;
import com.honeypot.common.model.exceptions.InvalidAuthorizationException;
import com.honeypot.common.model.exceptions.InvalidTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalApiExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> invalidTokenException(InvalidTokenException e) {
        return ErrorResponse.of(e, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidAuthorizationException.class)
    public ResponseEntity<ErrorResponse> InvalidAuthorizationException(InvalidAuthorizationException e) {
        return ErrorResponse.of(e, HttpStatus.FORBIDDEN);
    }

}
