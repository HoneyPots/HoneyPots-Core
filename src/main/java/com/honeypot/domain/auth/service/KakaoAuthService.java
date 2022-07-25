package com.honeypot.domain.auth.service;

import com.honeypot.domain.auth.dto.kakao.KakaoTokenInfo;
import com.honeypot.domain.auth.dto.kakao.KakaoTokenIssuance;
import com.honeypot.domain.auth.repository.KakaoAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoAuthService implements AuthService {

    private final KakaoAuthRepository kakaoAuthRepository;

    @Override
    public KakaoTokenInfo getTokenInfo(String authorizationCode) {
        KakaoTokenIssuance tokenInfo = kakaoAuthRepository.getAccessToken(authorizationCode);

        return kakaoAuthRepository.getTokenInfo(tokenInfo.getAccessToken());
    }

}
