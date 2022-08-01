package com.honeypot.domain.auth.api;

import com.honeypot.domain.auth.dto.LoginResponse;
import com.honeypot.domain.auth.dto.kakao.KakaoAuthCode;
import com.honeypot.domain.auth.service.contracts.AuthService;
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
        LoginResponse loginResponse = authService.loginWithOAuth("kakao", response.getCode());

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/login/{provider}")
    public ResponseEntity<?> login(@PathVariable String provider,
                                   @RequestParam String authorizationCode) {

        LoginResponse loginResponse = authService.loginWithOAuth(provider, authorizationCode);

        return ResponseEntity.ok(loginResponse);
    }
}
