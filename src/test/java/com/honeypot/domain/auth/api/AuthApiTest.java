package com.honeypot.domain.auth.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeypot.common.config.PropertiesConfig;
import com.honeypot.common.model.properties.JwtProperties;
import com.honeypot.domain.auth.dto.AuthCode;
import com.honeypot.domain.auth.dto.LoginResponse;
import com.honeypot.domain.auth.dto.RefreshTokenRequest;
import com.honeypot.domain.auth.service.contracts.AuthTokenManagerService;
import com.honeypot.domain.auth.service.contracts.LoginService;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.service.MemberFindService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.Cookie;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest(classes = {PropertiesConfig.class, ObjectMapper.class})
@ExtendWith({SpringExtension.class, MockitoExtension.class})
class AuthApiTest {

    @MockBean
    private LoginService loginService;

    @MockBean
    private AuthTokenManagerService authTokenManagerService;

    @MockBean
    private MemberFindService memberFindService;

    @Autowired
    private JwtProperties jwtProperties;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void before() {
        AuthApi authApi = new AuthApi(loginService, authTokenManagerService, memberFindService, jwtProperties);
        mockMvc = MockMvcBuilders.standaloneSetup(authApi).build();
    }

    @Test
    void kakao() throws Exception {
        // Arrange
        String authCode = "authCode";

        LoginResponse loginResponse = LoginResponse.builder()
                .memberId(1L)
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .isNewMember(false)
                .build();

        when(loginService.loginWithOAuth("kakao", authCode)).thenReturn(loginResponse);

        // Act
        ResultActions actions = mockMvc.perform(get("/api/auth/kakao")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("code", authCode)
        );

        // Assert
        actions.andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(jsonPath("memberId").value(loginResponse.getMemberId()))
                .andExpect(jsonPath("newMember").value(loginResponse.isNewMember()))
                .andExpect(jsonPath("accessToken").value(loginResponse.getAccessToken()))
                .andExpect(jsonPath("refreshToken").value(loginResponse.getRefreshToken()))
                .andDo(print());
    }

    @Test
    void login_kakao() throws Exception {
        // Arrange
        AuthCode authCode = new AuthCode();
        authCode.setAuthorizationCode("authCode");

        LoginResponse loginResponse = LoginResponse.builder()
                .memberId(1L)
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .isNewMember(false)
                .build();

        when(loginService.loginWithOAuth("kakao", authCode.getAuthorizationCode())).thenReturn(loginResponse);

        // Act
        ResultActions actions = mockMvc.perform(post("/api/auth/login/{provider}", "kakao")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(authCode))
        );

        // Assert
        actions.andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(jsonPath("memberId").value(loginResponse.getMemberId()))
                .andExpect(jsonPath("newMember").value(loginResponse.isNewMember()))
                .andExpect(jsonPath("accessToken").value(loginResponse.getAccessToken()))
                .andExpect(jsonPath("refreshToken").value(loginResponse.getRefreshToken()))
                .andDo(print());
    }

    @Test
    void refreshToken() throws Exception {
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

        // Act
        ResultActions actions = mockMvc.perform(post("/api/auth/token")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookie(cookie)
                .content(objectMapper.writeValueAsString(request))
        );

        // Assert
        actions.andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(jsonPath("memberId").value(loginResponse.getMemberId()))
                .andExpect(jsonPath("newMember").value(loginResponse.isNewMember()))
                .andExpect(jsonPath("accessToken").value(loginResponse.getAccessToken()))
                .andExpect(jsonPath("refreshToken").value(loginResponse.getRefreshToken()))
                .andDo(print());
    }

    @Test
    void refreshToken_204_NoContent_WhenCookieValueIsNull() throws Exception {
        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setGrantType("refresh_token");

        // Act
        ResultActions actions = mockMvc.perform(post("/api/auth/token")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        );

        // Assert
        actions.andExpect(status().isNoContent()).andDo(print());
    }

    @Test
    void refreshToken_400_BadRequest_WhenInvalidGrantType() {
        // Arrange
        String refreshTokenCookie = "refreshToken";
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setGrantType("invalid_grant_type");

        Cookie cookie = new Cookie("refreshToken", refreshTokenCookie);

        // Act & Assert
        assertThrows(NestedServletException.class, () -> {
            ResultActions actions = mockMvc.perform(post("/api/auth/token")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .cookie(cookie)
                    .content(objectMapper.writeValueAsString(request))
            );

            actions.andExpect(status().isBadRequest()).andDo(print());
        });
    }

    @Test
    void refreshToken_403_Forbidden_WhenInvalidTokenValue() throws Exception {
        // Arrange
        String refreshTokenValue = "refreshToken";
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setGrantType("refresh_token");

        when(authTokenManagerService.validate(refreshTokenValue)).thenReturn(false);

        Cookie cookie = new Cookie("refreshToken", refreshTokenValue);

        // Act
        ResultActions actions = mockMvc.perform(post("/api/auth/token")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookie(cookie)
                .content(objectMapper.writeValueAsString(request))
        );

        // Assert
        actions.andExpect(status().isForbidden()).andDo(print());
    }

    @Test
    void refreshToken_403_Forbidden_WhenMemberNotFound() throws Exception {
        // Arrange
        String refreshTokenValue = "refreshToken";
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setGrantType("refresh_token");

        Long memberId = 1L;

        when(authTokenManagerService.validate(refreshTokenValue)).thenReturn(true);
        when(authTokenManagerService.getMemberId(refreshTokenValue)).thenReturn(memberId);
        when(memberFindService.findById(memberId)).thenReturn(Optional.empty());

        Cookie cookie = new Cookie("refreshToken", refreshTokenValue);

        // Act
        ResultActions actions = mockMvc.perform(post("/api/auth/token")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookie(cookie)
                .content(objectMapper.writeValueAsString(request))
        );

        // Assert
        actions.andExpect(status().isForbidden()).andDo(print());
    }

    @Test
    void expireRefreshToken() throws Exception {
        // Arrange
        String refreshTokenCookie = "refreshToken";
        Cookie cookie = new Cookie("refreshToken", refreshTokenCookie);

        // Act
        ResultActions actions = mockMvc.perform(delete("/api/auth/token")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookie(cookie)
        );

        // Assert
        actions.andExpect(status().isForbidden()).andDo(print());
    }

}