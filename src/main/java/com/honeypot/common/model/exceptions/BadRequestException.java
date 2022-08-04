package com.honeypot.common.model.exceptions;

import java.util.Map;

public class BadRequestException extends BaseException {

    public BadRequestException(Map<String, String> badRequestList) {
        super("HRE001", badRequestList.toString());
    }
}
