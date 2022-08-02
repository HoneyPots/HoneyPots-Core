package com.honeypot.common.model.dto;

import com.honeypot.common.model.exceptions.BaseException;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {

    private String code;

    private String message;

    private LocalDateTime timestamp;

    public static ErrorResponse of(BaseException e) {
        return ErrorResponse.builder()
                .code(e.getCode())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ResponseEntity<ErrorResponse> of(BaseException e, HttpStatus statusCode) {
        return ResponseEntity
                .status(statusCode)
                .body(ErrorResponse.of(e));
    }

}
