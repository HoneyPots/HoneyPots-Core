package com.honeypot.domain.auth.service;

import com.honeypot.common.config.PropertiesConfig;
import com.honeypot.common.model.properties.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest(classes = {PropertiesConfig.class})
class JwtAuthTokenManagerServiceTest {

    @Autowired
    private JwtProperties jwtProperties;

    private JwtAuthTokenManagerService jwtAuthTokenManagerService;

    private static MockedStatic<LocalDateTime> localDateTimeMockedStatic;

    @BeforeAll
    public static void setup() {
        localDateTimeMockedStatic = mockStatic(LocalDateTime.class, CALLS_REAL_METHODS);
    }

    @BeforeEach
    public void before() {
        jwtAuthTokenManagerService = new JwtAuthTokenManagerService(jwtProperties);
    }

    @AfterAll
    public static void teardown() {
        localDateTimeMockedStatic.close();
    }

    @Test
    void issueAccessToken() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireAt = now.plus(jwtProperties.expirationTimeInSeconds().accessToken(), ChronoUnit.SECONDS);
        when(LocalDateTime.now()).thenReturn(now);

        Long memberId = 1L;
        String expected = Jwts
                .builder()
                .signWith(jwtProperties.generateSecretKey(), SignatureAlgorithm.HS512)
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(Timestamp.valueOf(now))
                .setExpiration(Timestamp.valueOf(expireAt))
                .compact();

        // Act
        String result = jwtAuthTokenManagerService.issueAccessToken(memberId);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void issueRefreshToken() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireAt = now.plus(jwtProperties.expirationTimeInSeconds().refreshToken(), ChronoUnit.SECONDS);
        when(LocalDateTime.now()).thenReturn(now);

        Long memberId = 1L;
        String expected = Jwts
                .builder()
                .signWith(jwtProperties.generateSecretKey(), SignatureAlgorithm.HS512)
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(Timestamp.valueOf(now))
                .setExpiration(Timestamp.valueOf(expireAt))
                .compact();

        // Act
        String result = jwtAuthTokenManagerService.issueRefreshToken(memberId);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void validate_WhenJwtIsValid() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireAt = now.plus(jwtProperties.expirationTimeInSeconds().refreshToken(), ChronoUnit.SECONDS);

        Long memberId = 1L;
        String accessToken = Jwts
                .builder()
                .signWith(jwtProperties.generateSecretKey(), SignatureAlgorithm.HS512)
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(Timestamp.valueOf(now))
                .setExpiration(Timestamp.valueOf(expireAt))
                .compact();
        // Act
        boolean result = jwtAuthTokenManagerService.validate(accessToken);

        // Assert
        assertTrue(result);
    }

    @Test
    void validate_WhenJwtWasExpired() {
        // Arrange
        LocalDateTime now = LocalDateTime.now().minusDays(1);

        Long memberId = 1L;
        String accessToken = Jwts
                .builder()
                .signWith(jwtProperties.generateSecretKey(), SignatureAlgorithm.HS512)
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(Timestamp.valueOf(now))
                .setExpiration(Timestamp.valueOf(now))
                .compact();

        // Act
        boolean result = jwtAuthTokenManagerService.validate(accessToken);

        // Assert
        assertFalse(result);
    }

    @Test
    void validate_WhenJwtClaimsIsNull() {
        // Act
        boolean result = jwtAuthTokenManagerService.validate(null);

        // Assert
        assertFalse(result);
    }

    @Test
    void validate_WhenMalformedJwt() {
        // Arrange
        LocalDateTime now = LocalDateTime.now().minusDays(1);

        Long memberId = 1L;
        String accessToken = Jwts
                .builder()
                .signWith(jwtProperties.generateSecretKey(), SignatureAlgorithm.HS512)
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(Timestamp.valueOf(now))
                .setExpiration(Timestamp.valueOf(now))
                .compact();

        // Act
        boolean result = jwtAuthTokenManagerService.validate(accessToken + "malformed");

        // Assert
        assertFalse(result);
    }

    @Test
    void getMemberId() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireAt = now.plus(jwtProperties.expirationTimeInSeconds().refreshToken(), ChronoUnit.SECONDS);

        Long memberId = 1L;
        String accessToken = Jwts
                .builder()
                .signWith(jwtProperties.generateSecretKey(), SignatureAlgorithm.HS512)
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(Timestamp.valueOf(now))
                .setExpiration(Timestamp.valueOf(expireAt))
                .compact();

        // Act
        Long result = jwtAuthTokenManagerService.getMemberId(accessToken);

        // Assert
        assertEquals(memberId, result);
    }

}