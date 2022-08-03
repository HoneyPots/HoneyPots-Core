package com.honeypot.domain.auth.service;

import com.honeypot.common.model.properties.JwtProperties;
import com.honeypot.domain.auth.service.contracts.AuthTokenManagerService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class JwtAuthTokenManagerService implements AuthTokenManagerService {

    private final JwtProperties jwtProperties;

    @Override
    public String issueAccessToken(@NotNull Long memberId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireAt = now.plus(jwtProperties.expirationTimeInSeconds().accessToken(), ChronoUnit.SECONDS);

        return Jwts
                .builder()
                .signWith(jwtProperties.generateSecretKey(), SignatureAlgorithm.HS512)
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(Timestamp.valueOf(now))
                .setExpiration(Timestamp.valueOf(expireAt))
                .compact();
    }

    @Override
    public String issueRefreshToken(@NotNull Long memberId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireAt = now.plus(jwtProperties.expirationTimeInSeconds().refreshToken(), ChronoUnit.SECONDS);

        return Jwts
                .builder()
                .signWith(jwtProperties.generateSecretKey(), SignatureAlgorithm.HS512)
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(Timestamp.valueOf(now))
                .setExpiration(Timestamp.valueOf(expireAt))
                .compact();
    }

    @Override
    public boolean validate(@NotNull String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtProperties.generateSecretKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature.", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.", e);
        } catch (IllegalArgumentException e) {
            log.info("Invalid JWT.", e);
        }

        return false;
    }

    @Override
    public Long getMemberId(@NotNull String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(jwtProperties.generateSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.valueOf(claims.getSubject());
    }

}
