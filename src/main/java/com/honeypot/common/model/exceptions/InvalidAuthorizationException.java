package com.honeypot.common.model.exceptions;

public class InvalidAuthorizationException extends BaseException {

    public InvalidAuthorizationException() {
        super("HAE002", "Authorization is not sufficient.");
    }

}
