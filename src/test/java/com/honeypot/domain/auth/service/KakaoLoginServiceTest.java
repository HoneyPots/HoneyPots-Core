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
import com.honeypot.domain.member.mapper.MemberMapper;
import com.honeypot.domain.member.service.MemberSignupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KakaoLoginServiceTest {

    private final MemberMapper memberMapper = Mappers.getMapper(MemberMapper.class);

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
        AuthProviderType provider = AuthProviderType.KAKAO;
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

        String providerMemberId = String.valueOf(kakaoTokenInfo.getId());

        Optional<AuthProvider> optionalAuthProvider = Optional.empty();

        AuthProvider newAuthProvider = AuthProvider.builder().providerMemberId(providerMemberId)
                .providerType(provider)
                .build();
        Member newMember = Member.builder()
                .id(1L)
                .authProvider(newAuthProvider)
                .build();

        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        when(kakaoAuthRepository.getAccessToken(authCode)).thenReturn(Mono.just(issuance));
        when(kakaoAuthRepository.getTokenInfo(issuance.getAccessToken())).thenReturn(Mono.just(kakaoTokenInfo));
        when(kakaoAuthRepository.getUserInfoByAccessToken(issuance.getAccessToken())).thenReturn(Mono.just(kakaoUserInfo));
        when(authProviderRepository.findByProviderTypeAndProviderMemberId(provider, providerMemberId))
                .thenReturn(optionalAuthProvider);
        when(memberSignupService.signupWithOAuth(providerMemberId, provider, kakaoUserInfo.getConnectedAt()))
                .thenAnswer(invocation -> {
                    newAuthProvider.setMember(newMember);
                    return memberMapper.toDto(newMember);
                });
        when(authTokenManagerService.issueAccessToken(newMember.getId())).thenReturn(accessToken);
        when(authTokenManagerService.issueRefreshToken(newMember.getId())).thenReturn(refreshToken);

        // Act
        Mono<LoginResponse> result = kakaoLoginService.loginWithOAuth(provider, authCode);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertTrue(response.isNewMember());
                    assertEquals(newMember.getId(), response.getMemberId());
                    assertEquals(accessToken, response.getAccessToken());
                    assertEquals(refreshToken, response.getRefreshToken());
                })
                .verifyComplete();
    }

    @Test
    void loginWithOAuth_WhenExistMemberLogin() {
        // Arrange
        AuthProviderType provider = AuthProviderType.KAKAO;
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

        String providerMemberId = String.valueOf(kakaoTokenInfo.getId());

        AuthProvider authProvider = AuthProvider.builder().providerMemberId(providerMemberId)
                .providerType(provider)
                .member(Member.builder().id(1L).build())
                .build();

        Optional<AuthProvider> optionalAuthProvider = Optional.of(authProvider);

        Long memberId = authProvider.getMember().getId();

        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        when(kakaoAuthRepository.getAccessToken(authCode)).thenReturn(Mono.just(issuance));
        when(kakaoAuthRepository.getTokenInfo(issuance.getAccessToken())).thenReturn(Mono.just(kakaoTokenInfo));
        when(kakaoAuthRepository.getUserInfoByAccessToken(issuance.getAccessToken())).thenReturn(Mono.just(kakaoUserInfo));
        when(authProviderRepository.findByProviderTypeAndProviderMemberId(provider, providerMemberId))
                .thenReturn(optionalAuthProvider);
        when(authTokenManagerService.issueAccessToken(memberId)).thenReturn(accessToken);
        when(authTokenManagerService.issueRefreshToken(memberId)).thenReturn(refreshToken);

        // Act
        Mono<LoginResponse> result = kakaoLoginService.loginWithOAuth(provider, authCode);

        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertFalse(response.isNewMember());
                    assertEquals(memberId, response.getMemberId());
                    assertEquals(accessToken, response.getAccessToken());
                    assertEquals(refreshToken, response.getRefreshToken());
                })
                .verifyComplete();
    }

}