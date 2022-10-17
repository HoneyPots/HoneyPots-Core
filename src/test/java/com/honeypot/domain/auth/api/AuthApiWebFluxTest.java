package com.honeypot.domain.auth.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeypot.common.config.PropertiesConfig;
import com.honeypot.domain.auth.dto.AuthCode;
import com.honeypot.domain.auth.dto.LoginResponse;
import com.honeypot.domain.auth.dto.RefreshTokenRequest;
import com.honeypot.domain.auth.entity.enums.AuthProviderType;
import com.honeypot.domain.auth.service.contracts.AuthTokenManagerService;
import com.honeypot.domain.auth.service.contracts.LoginService;
import com.honeypot.domain.member.entity.Member;
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

import javax.servlet.http.Cookie;
import java.util.Optional;

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

    @MockBean
    private AuthTokenManagerService authTokenManagerService;

    @MockBean
    private MemberFindService memberFindService;

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

    @Test
    void refreshToken() {
        // Arrange
        String refreshTokenCookie = "refreshToken";
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setGrantType("refresh_token");

        String newAccessToken = "newAccessToken";
        String newRefreshToken = "newRefreshToken";

        Member member = Member.builder().id(1L).nickname("nickname").build();
        LoginResponse loginResponse = LoginResponse.builder()
                .memberId(member.getId())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .isNewMember(false)
                .build();

        Cookie cookie = new Cookie("refreshToken", refreshTokenCookie);

        when(authTokenManagerService.validate(refreshTokenCookie)).thenReturn(true);
        when(authTokenManagerService.getMemberId(refreshTokenCookie)).thenReturn(member.getId());
        when(memberFindService.findById(member.getId())).thenReturn(Optional.of(member));
        when(authTokenManagerService.issueAccessToken(member.getId())).thenReturn(newAccessToken);
        when(authTokenManagerService.issueRefreshToken(member.getId())).thenReturn(newRefreshToken);

        // Act & Assert
        webTestClient
                .post()
                .uri("/api/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .cookie("refreshToken", cookie.getValue())
                .bodyValue(request)
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
    void refreshToken_204_NoContent_WhenCookieValueIsNull() {
        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setGrantType("refresh_token");

        // Act & Assert
        webTestClient
                .post()
                .uri("/api/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }


    @Test
    void refreshToken_400_BadRequest_WhenInvalidGrantType() {
        // Arrange
        String refreshTokenCookie = "refreshToken";
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setGrantType("invalid_grant_type");

        Cookie cookie = new Cookie("refreshToken", refreshTokenCookie);

        // Act & Assert
        webTestClient
                .post()
                .uri("/api/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .cookie("refreshToken", cookie.getValue())
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("code").isEqualTo("HRE001");
    }

    @Test
    void refreshToken_403_Forbidden_WhenInvalidTokenValue() {
        // Arrange
        String refreshTokenValue = "refreshToken";
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setGrantType("refresh_token");

        when(authTokenManagerService.validate(refreshTokenValue)).thenReturn(false);

        Cookie cookie = new Cookie("refreshToken", refreshTokenValue);

        // Act & Assert
        webTestClient
                .post()
                .uri("/api/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .cookie("refreshToken", cookie.getValue())
                .bodyValue(request)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody().isEmpty();
    }

    @Test
    void refreshToken_403_Forbidden_WhenMemberNotFound() {
        // Arrange
        String refreshTokenValue = "refreshToken";
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setGrantType("refresh_token");

        Long memberId = 1L;

        when(authTokenManagerService.validate(refreshTokenValue)).thenReturn(true);
        when(authTokenManagerService.getMemberId(refreshTokenValue)).thenReturn(memberId);
        when(memberFindService.findById(memberId)).thenReturn(Optional.empty());

        Cookie cookie = new Cookie("refreshToken", refreshTokenValue);

        // Act & Assert
        webTestClient
                .post()
                .uri("/api/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .cookie("refreshToken", cookie.getValue())
                .bodyValue(request)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody().isEmpty();
    }

    @Test
    void expireRefreshToken() {
        // Arrange
        Cookie cookie = new Cookie("refreshToken", "refreshToken");

        // Act & Assert
        webTestClient
                .delete()
                .uri("/api/auth/token")
                .accept(MediaType.APPLICATION_JSON)
                .cookie("refreshToken", cookie.getValue())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

}