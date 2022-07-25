package com.honeypot.domain.auth.service;

import com.honeypot.domain.auth.dto.LoginResponse;
import com.honeypot.domain.auth.dto.kakao.KakaoTokenInfo;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    KakaoTokenInfo getTokenInfo(String authorizationCode);

}
