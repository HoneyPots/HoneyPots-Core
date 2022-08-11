package com.honeypot.domain.auth.api;

import com.honeypot.common.model.exceptions.BadRequestException;
import com.honeypot.common.model.exceptions.RefreshFailedException;
import com.honeypot.common.model.properties.JwtProperties;
import com.honeypot.domain.auth.dto.AuthCode;
import com.honeypot.domain.auth.dto.LoginResponse;
import com.honeypot.domain.auth.dto.RefreshTokenRequest;
import com.honeypot.domain.auth.dto.kakao.KakaoAuthCode;
import com.honeypot.domain.auth.service.contracts.AuthTokenManagerService;
import com.honeypot.domain.auth.service.contracts.LoginService;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.service.MemberFindService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    private final JwtProperties jwtProperties;

    @Value("${domain.server.domain-name}")
    private String serverDomainName;

    @GetMapping("/kakao")
    public ResponseEntity<?> kakao(KakaoAuthCode authCode) {
        LoginResponse response = loginService.loginWithOAuth("kakao", authCode.getCode());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, getHttpOnlyCookie(response.getRefreshToken()).toString())
                .body(response);
    }

    @PostMapping("/login/{provider}")
    public ResponseEntity<?> login(@PathVariable String provider,
                                   @RequestBody AuthCode authorizationCode) {

        LoginResponse response = loginService.loginWithOAuth(provider, authorizationCode.getAuthorizationCode());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, getHttpOnlyCookie(response.getRefreshToken()).toString())
                .body(response);
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
                .memberId(memberId)
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, getHttpOnlyCookie(newRefreshToken).toString())
                .body(response);
    }

    private ResponseCookie getHttpOnlyCookie(String refreshToken) {
        return ResponseCookie
                .from("refreshToken", refreshToken)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(serverDomainName)
                .maxAge(jwtProperties.expirationTimeInSeconds().refreshToken())
                .build();
    }

}
