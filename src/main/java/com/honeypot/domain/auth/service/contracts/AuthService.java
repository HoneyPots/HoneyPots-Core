package com.honeypot.domain.auth.service.contracts;

import com.honeypot.domain.auth.dto.LoginResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    LoginResponse loginWithOAuth(String provider, String authorizationCode);

}