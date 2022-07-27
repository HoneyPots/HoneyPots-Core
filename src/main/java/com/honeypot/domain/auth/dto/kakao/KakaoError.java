package com.honeypot.domain.auth.dto.kakao;

import lombok.Data;

@Data
public class KakaoError {

    private String code;

    private String message;

}
