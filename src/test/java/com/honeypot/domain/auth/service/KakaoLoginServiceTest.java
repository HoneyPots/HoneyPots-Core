package com.honeypot.domain.auth.service;

import com.honeypot.domain.auth.dto.LoginResponse;
import com.honeypot.domain.auth.dto.kakao.KakaoTokenInfo;
import com.honeypot.domain.auth.dto.kakao.KakaoTokenIssuance;
import com.honeypot.domain.auth.dto.kakao.KakaoUserInfo;
import com.honeypot.domain.auth.entity.AuthProvider;
import com.honeypot.domain.auth.entity.enums.AuthProviderType;
import com.honeypot.domain.auth.repository.AuthProviderRepository;
import com.honeypot.domain.auth.repository.KakaoAuthRepository;
import com.honeypot.domain.auth.service.contracts.AuthTokenManagerService;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.service.MemberSignupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KakaoLoginServiceTest {

    @Mock
    private AuthTokenManagerService authTokenManagerService;

    @Mock
    private KakaoAuthRepository kakaoAuthRepository;

    @Mock
    private AuthProviderRepository authProviderRepository;

    @Mock
    private MemberSignupService memberSignupService;

    @InjectMocks
    private KakaoLoginService kakaoLoginService;

    @Test
    void loginWithOAuth_WhenNewMemberLogin() {
        // Arrange
        String provider = "kakao";
        String authCode = "authCode";

        KakaoTokenIssuance issuance = new KakaoTokenIssuance();
        issuance.setTokenType("Bearer");
        issuance.setAccessToken("accessToken");
        issuance.setExpiresIn(43199);
        issuance.setRefreshToken("refreshToken");
        issuance.setRefreshTokenExpiresIn(25184000);

        KakaoTokenInfo kakaoTokenInfo = new KakaoTokenInfo();
        kakaoTokenInfo.setId(1234566789);
        kakaoTokenInfo.setExpiresIn(7199);
        kakaoTokenInfo.setAppId(1234);

        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo();
        kakaoUserInfo.setConnectedAt(LocalDateTime.now());

        AuthProviderType providerType = AuthProviderType.valueOf(provider.toUpperCase());
        String providerMemberId = String.valueOf(kakaoTokenInfo.getId());

        Optional<AuthProvider> optionalAuthProvider = Optional.empty();

        Member newMember = Member.builder()
                .id(1L)
                .authProvider(AuthProvider.builder().providerMemberId(providerMemberId)
                        .providerType(providerType)
                        .build())
                .build();

        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        when(kakaoAuthRepository.getAccessToken(authCode)).thenReturn(issuance);
        when(kakaoAuthRepository.getTokenInfo(issuance.getAccessToken())).thenReturn(kakaoTokenInfo);
        when(kakaoAuthRepository.getUserInfoByAccessToken(issuance.getAccessToken())).thenReturn(kakaoUserInfo);
        when(authProviderRepository.findByProviderTypeAndProviderMemberId(providerType, providerMemberId))
                .thenReturn(optionalAuthProvider);
        when(memberSignupService.signupWithOAuth(providerMemberId, providerType, kakaoUserInfo.getConnectedAt()))
                .thenReturn(newMember);
        when(authTokenManagerService.issueAccessToken(newMember.getId())).thenReturn(accessToken);
        when(authTokenManagerService.issueRefreshToken(newMember.getId())).thenReturn(refreshToken);

        // Act
        LoginResponse result = kakaoLoginService.loginWithOAuth(provider, authCode);

        // Assert
        assertTrue(result.isNewMember());
        assertEquals(newMember.getId(), result.getMemberId());
        assertEquals(accessToken, result.getAccessToken());
        assertEquals(refreshToken, result.getRefreshToken());
    }

    @Test
    void loginWithOAuth_WhenExistMemberLogin() {
        // Arrange
        String provider = "kakao";
        String authCode = "authCode";

        KakaoTokenIssuance issuance = new KakaoTokenIssuance();
        issuance.setTokenType("Bearer");
        issuance.setAccessToken("accessToken");
        issuance.setExpiresIn(43199);
        issuance.setRefreshToken("refreshToken");
        issuance.setRefreshTokenExpiresIn(25184000);

        KakaoTokenInfo kakaoTokenInfo = new KakaoTokenInfo();
        kakaoTokenInfo.setId(1234566789);
        kakaoTokenInfo.setExpiresIn(7199);
        kakaoTokenInfo.setAppId(1234);

        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo();
        kakaoUserInfo.setConnectedAt(LocalDateTime.now());

        AuthProviderType providerType = AuthProviderType.valueOf(provider.toUpperCase());
        String providerMemberId = String.valueOf(kakaoTokenInfo.getId());

        AuthProvider authProvider = AuthProvider.builder().providerMemberId(providerMemberId)
                .providerType(providerType)
                .member(Member.builder().id(1L).build())
                .build();

        Optional<AuthProvider> optionalAuthProvider = Optional.of(authProvider);

        Long memberId = authProvider.getMember().getId();

        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        when(kakaoAuthRepository.getAccessToken(authCode)).thenReturn(issuance);
        when(kakaoAuthRepository.getTokenInfo(issuance.getAccessToken())).thenReturn(kakaoTokenInfo);
        when(kakaoAuthRepository.getUserInfoByAccessToken(issuance.getAccessToken())).thenReturn(kakaoUserInfo);
        when(authProviderRepository.findByProviderTypeAndProviderMemberId(providerType, providerMemberId))
                .thenReturn(optionalAuthProvider);
        when(authTokenManagerService.issueAccessToken(memberId)).thenReturn(accessToken);
        when(authTokenManagerService.issueRefreshToken(memberId)).thenReturn(refreshToken);

        // Act
        LoginResponse result = kakaoLoginService.loginWithOAuth(provider, authCode);

        // Assert
        assertFalse(result.isNewMember());
        assertEquals(memberId, result.getMemberId());
        assertEquals(accessToken, result.getAccessToken());
        assertEquals(refreshToken, result.getRefreshToken());
    }

}