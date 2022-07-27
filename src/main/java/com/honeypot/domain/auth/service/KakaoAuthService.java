package com.honeypot.domain.auth.service;

import com.honeypot.domain.auth.dto.LoginResponse;
import com.honeypot.domain.auth.dto.kakao.KakaoTokenIssuance;
import com.honeypot.domain.auth.dto.kakao.KakaoUserInfo;
import com.honeypot.domain.auth.repository.KakaoAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoAuthService implements AuthService {

    private final KakaoAuthRepository kakaoAuthRepository;

    @Override
    public LoginResponse loginWithKakao(String authorizationCode) {
        KakaoTokenIssuance token = kakaoAuthRepository.getAccessToken(authorizationCode);
        KakaoUserInfo kakaoUserInfo = kakaoAuthRepository.getUserInfoByAccessToken(token.getAccessToken());
        return LoginResponse.builder()
                .kakaoUserInfo(kakaoUserInfo)
                .build();
    }

}
