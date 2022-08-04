package com.honeypot.domain.auth.service.contracts;

import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public interface AuthTokenManagerService {

    String issueAccessToken(@NotNull Long memberId);

    String issueRefreshToken(@NotNull Long memberId);

    boolean validate(@NotNull String token);

    Long getMemberId(@NotNull String token);

}
