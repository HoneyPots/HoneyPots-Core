package com.honeypot.common.model.properties;

import com.honeypot.common.config.PropertiesConfig;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(classes = {PropertiesConfig.class})
class JwtPropertiesTest {

    @Autowired
    private Environment env;

    @Autowired
    private JwtProperties jwtProperties;

    @Test
    void header() {
        assertEquals(env.getProperty("jwt.header"), jwtProperties.header());
    }

    @Test
    void secretKey() {
        assertEquals(env.getProperty("jwt.secret-key"), jwtProperties.secretKey());
    }

    @Test
    void expirationTimeInSecond() {
        String prefix = "jwt.expiration-time-in-seconds.";
        assertEquals(env.getProperty(prefix + "access-token"),
                String.valueOf(jwtProperties.expirationTimeInSeconds().accessToken()));
        assertEquals(env.getProperty(prefix + "refresh-token"),
                String.valueOf(jwtProperties.expirationTimeInSeconds().refreshToken()));
    }

    @Test
    void generateSecretKey() {
        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(env.getProperty("jwt.secret-key")));
        assertEquals(secretKey, jwtProperties.generateSecretKey());
    }

}