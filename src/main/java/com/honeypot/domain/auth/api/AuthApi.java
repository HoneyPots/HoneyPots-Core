package com.honeypot.domain.auth.api;

import com.honeypot.common.model.exceptions.BadRequestException;
import com.honeypot.common.model.properties.JwtProperties;
import com.honeypot.domain.auth.dto.AuthCode;
import com.honeypot.domain.auth.dto.LoginResponse;
import com.honeypot.domain.auth.dto.RefreshTokenRequest;
import com.honeypot.domain.auth.dto.kakao.KakaoAuthCode;
import com.honeypot.domain.auth.entity.enums.AuthProviderType;
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
import reactor.core.publisher.Mono;

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
    public Mono<ResponseEntity<LoginResponse>> kakao(KakaoAuthCode authCode) {
        long maxAge = jwtProperties.expirationTimeInSeconds().refreshToken();
        Mono<LoginResponse> response = loginService.loginWithOAuth(AuthProviderType.KAKAO, authCode.getCode());
        return response
                .map(r -> ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE,
                                getHttpOnlyRefreshTokenCookie(r.getRefreshToken(), maxAge).toString()
                        )
                        .body(r)
                );
    }

    @PostMapping("/login/{provider}")
    public Mono<ResponseEntity<LoginResponse>> login(@PathVariable String provider,
                                                     @RequestBody AuthCode authorizationCode) {
        AuthProviderType providerType = AuthProviderType.valueOf(provider.toUpperCase());
        long maxAge = jwtProperties.expirationTimeInSeconds().refreshToken();
        Mono<LoginResponse> response = loginService.loginWithOAuth(providerType, authorizationCode.getAuthorizationCode());
        return response
                .map(r -> ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE,
                                getHttpOnlyRefreshTokenCookie(r.getRefreshToken(), maxAge).toString()
                        )
                        .body(r)
                );
    }

    @PostMapping("/token")
    public Mono<ResponseEntity<LoginResponse>> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                                            @RequestBody RefreshTokenRequest request) {
        if (!request.getGrantType().equals("refresh_token")) {
            Map<String, String> errorMessages = new HashMap<>();
            errorMessages.put("grantType", "grantType is must be 'refresh_token'");
            throw new BadRequestException(errorMessages);
        }

        if (refreshToken == null) {
            return Mono.just(ResponseEntity.noContent().build());
        }

        if (!authTokenManagerService.validate(refreshToken)) {
            return Mono.just(getExpiredRefreshTokenResponse(refreshToken));
        }

        Long memberId = authTokenManagerService.getMemberId(refreshToken);
        if (memberFindService.findById(memberId).isEmpty()) {
            return Mono.just(getExpiredRefreshTokenResponse(refreshToken));
        }

        long maxAge = jwtProperties.expirationTimeInSeconds().refreshToken();
        String newAccessToken = authTokenManagerService.issueAccessToken(memberId);
        String newRefreshToken = authTokenManagerService.issueRefreshToken(memberId);

        return Mono.just(
                ResponseEntity
                        .ok()
                        .header(HttpHeaders.SET_COOKIE, getHttpOnlyRefreshTokenCookie(newRefreshToken, maxAge).toString())
                        .body(LoginResponse.builder()
                                .memberId(memberId)
                                .accessToken(newAccessToken)
                                .refreshToken(newRefreshToken)
                                .build()
                        )
        );
    }

    @DeleteMapping("/token")
    public ResponseEntity<?> expireRefreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .header(HttpHeaders.SET_COOKIE, getHttpOnlyRefreshTokenCookie(refreshToken, 0).toString())
                .build();
    }

    private ResponseEntity<LoginResponse> getExpiredRefreshTokenResponse(String refreshToken) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .header(HttpHeaders.SET_COOKIE, getHttpOnlyRefreshTokenCookie(refreshToken, 0).toString())
                .build();
    }

    private ResponseCookie getHttpOnlyRefreshTokenCookie(String refreshToken, long maxAge) {
        return ResponseCookie
                .from("refreshToken", refreshToken)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(serverDomainName)
                .maxAge(maxAge)
                .build();
    }

}
