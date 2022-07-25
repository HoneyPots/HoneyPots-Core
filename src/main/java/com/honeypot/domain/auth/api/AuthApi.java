package com.honeypot.domain.auth.api;

import com.honeypot.domain.auth.dto.LoginResponse;
import com.honeypot.domain.auth.dto.kakao.KakaoAuthCode;
import com.honeypot.domain.auth.dto.kakao.KakaoTokenInfo;
import com.honeypot.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthApi {

    private final AuthService authService;

    @GetMapping("/kakao")
    public ResponseEntity<?> kakao(KakaoAuthCode response) {

        KakaoTokenInfo tokenInfo = authService.getTokenInfo(response.getCode());

        return ResponseEntity.ok(tokenInfo);
    }

}
