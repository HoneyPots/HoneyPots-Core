package com.honeypot.domain.auth.dto;

import com.honeypot.domain.auth.dto.kakao.KakaoUserInfo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private KakaoUserInfo kakaoUserInfo;

}
