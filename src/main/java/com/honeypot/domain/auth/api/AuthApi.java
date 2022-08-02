package com.honeypot.domain.auth.api;

import com.honeypot.domain.auth.dto.LoginResponse;
import com.honeypot.domain.auth.dto.kakao.KakaoAuthCode;
import com.honeypot.domain.auth.service.contracts.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthApi {

    private final LoginService loginService;

    @GetMapping("/kakao")
    public ResponseEntity<?> kakao(KakaoAuthCode response) {
        LoginResponse loginResponse = loginService.loginWithOAuth("kakao", response.getCode());

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/login/{provider}")
    public ResponseEntity<?> login(@PathVariable String provider,
                                   @RequestParam String authorizationCode) {

        LoginResponse loginResponse = loginService.loginWithOAuth(provider, authorizationCode);

        return ResponseEntity.ok(loginResponse);
    }
}
