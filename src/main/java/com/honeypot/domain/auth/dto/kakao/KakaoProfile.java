package com.honeypot.domain.auth.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoProfile {

    private String nickname;

    private String thumbnailImageUrl;

    private String profileImageUrl;

    @JsonProperty("is_default_image")
    private boolean isDefaultImage;

}
