package com.honeypot.domain.auth.dto;

import lombok.Data;

@Data
public class RefreshTokenRequest {

    private String grantType;

}
