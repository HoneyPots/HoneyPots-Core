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
import com.honeypot.domain.auth.service.contracts.LoginService;
import com.honeypot.domain.member.service.MemberSignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoLoginService implements LoginService {

    private final AuthTokenManagerService authTokenManagerService;

    private final KakaoAuthRepository kakaoAuthRepository;

    private final AuthProviderRepository authProviderRepository;

    private final MemberSignupService memberSignupService;

    @Transactional
    @Override
    public LoginResponse loginWithOAuth(String provider, String authorizationCode) {
        /*
        1. authorizationCode를 이용해 카카오측에 Access token 발급 요청
        2. Access Token이 정상적으로 발급되었다면, 카카오 로그인 인증 완료된 것
        3. 발행받은 Access token을 이용해 User id 획득
        4. 획득한 User id를 이용해 서비스 가입 여부를 판단
        5. 서비스 가입 여부에 따라 분기
            5.1 미가입 사용자 : 카카오측에 Userinfo 요청하여 서비스 가입 수행
            5.2 기가입 사용자 : Goto 6
        6. 서비스 DB에 저장된 사용자 정보 획득 및 서비스측 Access token & Refresh token 발행
        7. LoginResponse 응답
         */
        KakaoTokenIssuance token = kakaoAuthRepository.getAccessToken(authorizationCode);

        String kakaoAccessToken = token.getAccessToken();
        KakaoTokenInfo tokenInfo = kakaoAuthRepository.getTokenInfo(kakaoAccessToken);
        KakaoUserInfo userInfo = kakaoAuthRepository.getUserInfoByAccessToken(kakaoAccessToken);

        AuthProviderType providerType = AuthProviderType.valueOf(provider.toUpperCase());
        String providerMemberId = String.valueOf(tokenInfo.getId());

        // find exist member or signup.
        boolean isNewMember = false;
        Optional<AuthProvider> authProviderOptional = authProviderRepository
                .findByProviderTypeAndProviderMemberId(providerType, providerMemberId);

        AuthProvider authProvider;
        if (authProviderOptional.isPresent()) {
            authProvider = authProviderOptional.get();
        } else {
            authProvider = memberSignupService
                    .signupWithOAuth(providerMemberId, providerType, userInfo.getConnectedAt())
                    .getAuthProvider();

            isNewMember = true;
        }

        Long memberId = authProvider.getMember().getId();
        String accessToken = authTokenManagerService.issueAccessToken(memberId);
        String refreshToken = authTokenManagerService.issueRefreshToken(memberId);

        return LoginResponse.builder()
                .memberId(memberId)
                .isNewMember(isNewMember)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
