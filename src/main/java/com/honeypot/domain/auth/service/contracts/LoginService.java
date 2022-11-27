package com.honeypot.domain.auth.service.contracts;

import com.honeypot.domain.auth.dto.LoginResponse;
import com.honeypot.domain.auth.entity.enums.AuthProviderType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface LoginService {

    Mono<LoginResponse> loginWithOAuth(AuthProviderType provider, String authorizationCode);

}
