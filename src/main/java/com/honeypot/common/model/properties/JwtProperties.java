package com.honeypot.common.model.properties;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String header,
                            String secretKey,
                            ExpirationTime expirationTimeInSeconds) {

    private static SecretKey key;

    public SecretKey generateSecretKey() {
        if (key != null) {
            return key;
        }
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public record ExpirationTime(long accessToken,
                                 long refreshToken) {
    }
}
