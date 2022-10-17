package com.honeypot.domain.auth.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeypot.common.config.PropertiesConfig;
import com.honeypot.domain.auth.dto.AuthCode;
import com.honeypot.domain.auth.dto.LoginResponse;
import com.honeypot.domain.auth.entity.enums.AuthProviderType;
import com.honeypot.domain.auth.service.contracts.AuthTokenManagerService;
import com.honeypot.domain.auth.service.contracts.LoginService;
import com.honeypot.domain.member.service.MemberFindService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@WebFluxTest(
        value = {AuthApi.class, PropertiesConfig.class, ObjectMapper.class},
        excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class}
)
@MockBean(value = {
        JpaMetamodelMappingContext.class,
        AuthTokenManagerService.class,
        MemberFindService.class
})
@ExtendWith(MockitoExtension.class)
class AuthApiWebFluxTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    private LoginService loginService;

    @Value("${domain.server.domain-name}")
    private String serverDomainName;

    @Test
    void kakao() {
        // Arrange
        String authCode = "authCode";

        LoginResponse loginResponse = LoginResponse.builder()
                .memberId(1L)
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .isNewMember(false)
                .build();

        when(loginService.loginWithOAuth(AuthProviderType.KAKAO, authCode)).thenReturn(Mono.just(loginResponse));

        webTestClient
                .get()
                .uri("/api/auth/kakao?code=" + authCode)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists(HttpHeaders.SET_COOKIE)
                .expectBody(LoginResponse.class)
                .value(response -> {
                    assertEquals(loginResponse.getMemberId(), response.getMemberId());
                    assertEquals(loginResponse.isNewMember(), response.isNewMember());
                    assertEquals(loginResponse.getAccessToken(), response.getAccessToken());
                    assertEquals(loginResponse.getRefreshToken(), response.getRefreshToken());
                });
    }

    @Test
    void login_kakao() {
        // Arrange
        AuthCode authCode = new AuthCode();
        authCode.setAuthorizationCode("authCode");

        LoginResponse loginResponse = LoginResponse.builder()
                .memberId(1L)
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .isNewMember(false)
                .build();

        when(loginService.loginWithOAuth(
                AuthProviderType.KAKAO,
                authCode.getAuthorizationCode()
        )).thenReturn(Mono.just(loginResponse));

        // Act & Assert
        webTestClient
                .post()
                .uri("/api/auth/login/kakao")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(authCode)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists(HttpHeaders.SET_COOKIE)
                .expectBody(LoginResponse.class)
                .value(response -> {
                    assertEquals(loginResponse.getMemberId(), response.getMemberId());
                    assertEquals(loginResponse.isNewMember(), response.isNewMember());
                    assertEquals(loginResponse.getAccessToken(), response.getAccessToken());
                    assertEquals(loginResponse.getRefreshToken(), response.getRefreshToken());
                });
    }

}