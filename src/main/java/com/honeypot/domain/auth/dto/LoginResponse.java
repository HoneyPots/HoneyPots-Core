package com.honeypot.domain.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private Long memberId;

    private boolean isNewMember;

    private String accessToken;

    private String refreshToken;

}
