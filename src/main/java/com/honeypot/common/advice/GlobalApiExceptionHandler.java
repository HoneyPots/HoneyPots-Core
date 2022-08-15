package com.honeypot.common.advice;

import com.honeypot.common.model.dto.ErrorResponse;
import com.honeypot.common.model.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    @ExceptionHandler(RefreshFailedException.class)
    public ResponseEntity<ErrorResponse> refreshFailedException(RefreshFailedException e) {
        return ErrorResponse.of(e, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> badRequestException(BadRequestException e) {
        return ErrorResponse.of(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ReportTargetNotFoundException.class)
    public ResponseEntity<ErrorResponse> reportTargetNotFoundException(ReportTargetNotFoundException e) {
        return ErrorResponse.of(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> constraintViolationException(ConstraintViolationException e) {
        Map<String, String> errorMessages = new HashMap<>();
        List<ConstraintViolation<?>> list = e.getConstraintViolations().stream().toList();
        for (ConstraintViolation<?> cv : list) {
            String property = cv.getPropertyPath().toString();
            property = property.substring(property.lastIndexOf(".") + 1);
            errorMessages.put(property, cv.getMessage());
        }
        return ErrorResponse.of(new BadRequestException(errorMessages), HttpStatus.BAD_REQUEST);
    }

}
