package com.honeypot.domain.auth.api;

import com.honeypot.common.model.exceptions.BadRequestException;
import com.honeypot.common.model.exceptions.RefreshFailedException;
import com.honeypot.domain.auth.dto.LoginResponse;
import com.honeypot.domain.auth.dto.RefreshTokenRequest;
import com.honeypot.domain.auth.dto.kakao.KakaoAuthCode;
import com.honeypot.domain.auth.service.contracts.AuthTokenManagerService;
import com.honeypot.domain.auth.service.contracts.LoginService;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.service.MemberFindService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthApi {

    private final LoginService loginService;

    private final AuthTokenManagerService authTokenManagerService;

    private final MemberFindService memberFindService;

    @GetMapping("/kakao")
    public ResponseEntity<?> kakao(KakaoAuthCode response) {
        LoginResponse loginResponse = loginService.loginWithOAuth("kakao", response.getCode());

        ResponseCookie cookie = ResponseCookie
                .from("refreshToken", loginResponse.getRefreshToken())
                .path("/")
                .secure(false)
                .httpOnly(true)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginResponse);
    }

    @GetMapping("/login/{provider}")
    public ResponseEntity<?> login(@PathVariable String provider,
                                   @RequestParam String authorizationCode) {

        LoginResponse loginResponse = loginService.loginWithOAuth(provider, authorizationCode);

        ResponseCookie cookie = ResponseCookie
                .from("refreshToken", loginResponse.getRefreshToken())
                .path("/")
                .secure(false)
                .httpOnly(true)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginResponse);
    }

    @PostMapping("/token")
    public ResponseEntity<?> refreshToken(@CookieValue("refreshToken") String refreshToken,
                                          @RequestBody RefreshTokenRequest request) {

        if (!request.getGrantType().equals("refresh_token")) {
            Map<String, String> errorMessages = new HashMap<>();
            errorMessages.put("grantType", "grantType is must me 'refresh_token'");
            throw new BadRequestException(errorMessages);
        }

        if (!authTokenManagerService.validate(refreshToken)) {
            throw new RefreshFailedException();
        }

        Long memberId = authTokenManagerService.getMemberId(refreshToken);
        Member member = memberFindService.findById(memberId)
                .orElseThrow(RefreshFailedException::new);

        String newAccessToken = authTokenManagerService.issueAccessToken(member.getId());
        String newRefreshToken = authTokenManagerService.issueRefreshToken(member.getId());

        LoginResponse response = LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();

        return ResponseEntity.ok(response);
    }
}
