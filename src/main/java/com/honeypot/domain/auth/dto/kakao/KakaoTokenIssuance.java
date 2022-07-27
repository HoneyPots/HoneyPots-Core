package com.honeypot.domain.auth.dto.kakao;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoTokenIssuance {

    private String tokenType;

    private String accessToken;

    private String idToken;

    private int expiresIn;

    private String refreshToken;

    private int refreshTokenExpiresIn;

    private String scope;

}
