package com.honeypot.domain.auth.service;

import com.honeypot.domain.auth.dto.kakao.KakaoTokenInfoResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    KakaoTokenInfoResponse getTokenInfo(String authorizationCode);

}
