package com.honeypot.common.model.exceptions;

public class RefreshFailedException extends BaseException{

    public RefreshFailedException() {
        super("HAE003", "Refresh token has already expired. Please log in again.");
    }
}
