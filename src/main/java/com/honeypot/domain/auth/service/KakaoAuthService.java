package com.honeypot.domain.auth.service;

import com.honeypot.domain.auth.dto.LoginResponse;
import com.honeypot.domain.auth.dto.kakao.KakaoTokenInfo;
import com.honeypot.domain.auth.dto.kakao.KakaoTokenIssuance;
import com.honeypot.domain.auth.dto.kakao.KakaoUserInfo;
import com.honeypot.domain.auth.repository.KakaoAuthRepository;
import com.honeypot.domain.member.entity.AuthProvider;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.enums.AuthProviderType;
import com.honeypot.domain.member.repository.AuthProviderRepository;
import com.honeypot.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class KakaoAuthService implements AuthService {

    private final TokenManagerService tokenManagerService;

    private final KakaoAuthRepository kakaoAuthRepository;

    private final AuthProviderRepository authProviderRepository;

    private final MemberRepository memberRepository;

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
        6. 서비스 DB에 저장된 사용자 정보 획득 및 서비스측 Access token 발행
        7. LoginResponse 응답
         */
        KakaoTokenIssuance token = kakaoAuthRepository.getAccessToken(authorizationCode);

        String accessToken = token.getAccessToken();
        KakaoTokenInfo tokenInfo = kakaoAuthRepository.getTokenInfo(accessToken);
        KakaoUserInfo userInfo = kakaoAuthRepository.getUserInfoByAccessToken(accessToken);

        AuthProviderType providerType = AuthProviderType.valueOf(provider.toUpperCase());
        long providerMemberId = tokenInfo.getId();

        AuthProvider authProvider = authProviderRepository.findByProviderTypeAndProviderMemberId(providerType, providerMemberId);
        if (authProvider == null) {
            Member member = Member.builder()
                    .nickname("헤리움" + Timestamp.valueOf(LocalDateTime.now()).getTime())
                    .build();

            memberRepository.save(member);

            authProvider = AuthProvider.builder()
                    .member(member)
                    .providerMemberId(providerMemberId)
                    .providerType(providerType)
                    .accessToken(accessToken)
                    .connectDate(userInfo.getConnectedAt())
                    .build();

            authProviderRepository.save(authProvider);
        }

        String serviceToken = tokenManagerService.issueToken(authProvider.getId());

        return LoginResponse.builder()
                .accessToken(serviceToken)
                .build();
    }
}
