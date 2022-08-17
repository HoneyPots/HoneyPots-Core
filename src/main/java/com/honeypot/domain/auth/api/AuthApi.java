package com.honeypot.domain.auth.api;

import com.honeypot.common.model.exceptions.BadRequestException;
import com.honeypot.common.model.properties.JwtProperties;
import com.honeypot.domain.auth.dto.AuthCode;
import com.honeypot.domain.auth.dto.LoginResponse;
import com.honeypot.domain.auth.dto.RefreshTokenRequest;
import com.honeypot.domain.auth.dto.kakao.KakaoAuthCode;
import com.honeypot.domain.auth.service.contracts.AuthTokenManagerService;
import com.honeypot.domain.auth.service.contracts.LoginService;
import com.honeypot.domain.member.service.MemberFindService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
                .header(HttpHeaders.SET_COOKIE, getHttpOnlyCookie(response.getRefreshToken(), false).toString())
                .body(response);
    }

    @PostMapping("/login/{provider}")
    public ResponseEntity<?> login(@PathVariable String provider,
                                   @RequestBody AuthCode authorizationCode) {

        LoginResponse response = loginService.loginWithOAuth(provider, authorizationCode.getAuthorizationCode());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, getHttpOnlyCookie(response.getRefreshToken(), false).toString())
                .body(response);
    }

    @PostMapping("/token")
    public ResponseEntity<?> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                          @RequestBody RefreshTokenRequest request) {

        if (!request.getGrantType().equals("refresh_token")) {
            Map<String, String> errorMessages = new HashMap<>();
            errorMessages.put("grantType", "grantType is must be 'refresh_token'");
            throw new BadRequestException(errorMessages);
        }

        if (refreshToken == null) {
            return ResponseEntity.noContent().build();
        }

        if (!authTokenManagerService.validate(refreshToken)) {
            return getExpiredRefreshTokenResponse(refreshToken);
//            throw new RefreshFailedException();
        }

        Long memberId = authTokenManagerService.getMemberId(refreshToken);
        if (memberFindService.findById(memberId).isEmpty()) {
            return getExpiredRefreshTokenResponse(refreshToken);
        }

        String newAccessToken = authTokenManagerService.issueAccessToken(memberId);
        String newRefreshToken = authTokenManagerService.issueRefreshToken(memberId);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, getHttpOnlyCookie(newRefreshToken, false).toString())
                .body(LoginResponse.builder()
                        .memberId(memberId)
                        .accessToken(newAccessToken)
                        .refreshToken(newRefreshToken)
                        .build()
                );
    }

    @DeleteMapping("/token")
    public ResponseEntity<?> expireRefreshToken(@CookieValue("refreshToken") String refreshToken) {
        return getExpiredRefreshTokenResponse(refreshToken);
    }

    private ResponseEntity<?> getExpiredRefreshTokenResponse(String refreshToken) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .header(HttpHeaders.SET_COOKIE, getHttpOnlyCookie(refreshToken, true).toString())
                .build();
    }

    private ResponseCookie getHttpOnlyCookie(String refreshToken, boolean isExpired) {
        return ResponseCookie
                .from("refreshToken", refreshToken)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(serverDomainName)
                .maxAge(isExpired ? 0 : jwtProperties.expirationTimeInSeconds().refreshToken())
                .build();
    }

}
