package com.honeypot.common.model.exceptions;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final String code;

    public BaseException(String code, String message) {
        super(message);
        this.code = code;
    }

}
