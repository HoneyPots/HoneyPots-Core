package com.honeypot.common.model.exceptions;

public class InvalidTokenException extends BaseException {

    public InvalidTokenException() {
        super("HAE001", "Authentication token is invalid. please check token value or refresh.");
    }

}
