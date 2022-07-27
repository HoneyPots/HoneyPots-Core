package com.honeypot.domain.auth.dto.kakao;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.honeypot.common.model.properties.KakaoProperties;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoUserInfo {

    private long id;

    private boolean hasSignedUp;

    private LocalDateTime connectedAt;

    private LocalDateTime synchedAt;

    private KakaoUserProperties properties;

    private KakaoAccount kakaoAccount;

}
