package com.honeypot.domain.auth.repository;

import com.honeypot.common.model.properties.KakaoProperties;
import com.honeypot.domain.auth.dto.kakao.KakaoTokenIssuance;
import com.honeypot.domain.auth.dto.kakao.KakaoTokenInfo;
import com.honeypot.domain.auth.dto.kakao.KakaoUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Repository
@RequiredArgsConstructor
public class KakaoAuthRepository {

    private static final String BEARER_TYPE = "Bearer ";

    private final WebClient webClient;

    private final KakaoProperties kakaoProperties;

    public KakaoTokenIssuance getAccessToken(String authorizationCode) {
        return webClient
                .post()
                .uri(uriBuilder -> UriComponentsBuilder
                        .fromUriString(kakaoProperties.apiPath().getAccessToken())
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", kakaoProperties.apiKey().restApi())
                        .queryParam("redirect_uri", kakaoProperties.redirectUrl())
                        .queryParam("code", authorizationCode)
                        .queryParam("client_secret", kakaoProperties.clientSecret())
                        .build()
                        .toUri())
                .header(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_FORM_URLENCODED))
                .retrieve()
                .bodyToMono(KakaoTokenIssuance.class)
                .block();
    }

    public KakaoTokenInfo getTokenInfo(String accessToken) {
        return webClient
                .get()
                .uri(kakaoProperties.apiPath().getAccessTokenInfo())
                .header(HttpHeaders.AUTHORIZATION, BEARER_TYPE + accessToken)
                .retrieve()
                .bodyToMono(KakaoTokenInfo.class)
                .block();
    }

    public KakaoUserInfo getUserInfoByAccessToken(String accessToken) {
        return webClient
                .get()
                .uri(kakaoProperties.apiPath().getUserInfoByAccessToken())
                .header(HttpHeaders.AUTHORIZATION, BEARER_TYPE + accessToken)
                .retrieve()
                .bodyToMono(KakaoUserInfo.class)
                .block();
    }
}
