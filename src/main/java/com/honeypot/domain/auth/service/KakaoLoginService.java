package com.honeypot.domain.auth.service;

import com.honeypot.domain.auth.dto.LoginResponse;
import com.honeypot.domain.auth.entity.AuthProvider;
import com.honeypot.domain.auth.entity.enums.AuthProviderType;
import com.honeypot.domain.auth.repository.AuthProviderRepository;
import com.honeypot.domain.auth.repository.KakaoAuthRepository;
import com.honeypot.domain.auth.service.contracts.AuthTokenManagerService;
import com.honeypot.domain.auth.service.contracts.LoginService;
import com.honeypot.domain.member.service.MemberSignupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoLoginService implements LoginService {

    private final AuthTokenManagerService authTokenManagerService;

    private final KakaoAuthRepository kakaoAuthRepository;

    private final AuthProviderRepository authProviderRepository;

    private final MemberSignupService memberSignupService;

    @Transactional
    @Override
    public Mono<LoginResponse> loginWithOAuth(AuthProviderType provider, String authorizationCode) {
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
        return kakaoAuthRepository.getAccessToken(authorizationCode)
                .flatMap(t -> kakaoAuthRepository.getTokenInfo(t.getAccessToken())
                        .zipWith(kakaoAuthRepository.getUserInfoByAccessToken(t.getAccessToken())
                                .subscribeOn(Schedulers.boundedElastic()))
                        .flatMap(tuple2 -> {
                            String providerMemberId = String.valueOf(tuple2.getT1().getId());
                            LocalDateTime connectedAt = tuple2.getT2().getConnectedAt();
                            return Mono.just(getLoginResponse(providerMemberId, provider, connectedAt));
                        })
                );
    }

    @Transactional
    private LoginResponse getLoginResponse(String providerMemberId,
                                           AuthProviderType providerType,
                                           LocalDateTime connectedAt
    ) {
        Optional<AuthProvider> authProviderOptional = authProviderRepository
                .findByProviderTypeAndProviderMemberId(providerType, providerMemberId);

        Long memberId = authProviderOptional
                .orElseGet(() -> memberSignupService
                        .signupWithOAuth(providerMemberId, providerType, connectedAt)
                        .getAuthProvider()
                )
                .getMember()
                .getId();

        String accessToken = authTokenManagerService.issueAccessToken(memberId);
        String refreshToken = authTokenManagerService.issueRefreshToken(memberId);

        return LoginResponse.builder()
                .memberId(memberId)
                .isNewMember(authProviderOptional.isEmpty())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
