package com.honeypot.common.model.properties;

import com.honeypot.common.config.PropertiesConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(classes = {PropertiesConfig.class})
class KakaoPropertiesTest {

    @Autowired
    private Environment env;

    @Autowired
    private KakaoProperties kakaoProperties;

    @Test
    void kakaoProperties() {

        String prefix = "oauth.kakao.";
        assertEquals(env.getProperty(prefix + "client-secret"), kakaoProperties.clientSecret());
        assertEquals(env.getProperty(prefix + "redirect-url"), kakaoProperties.redirectUrl());

        String apiKeyPrefix = prefix + "api-key.";
        KakaoProperties.KakaoApiKeyProperties apiKeyProperties = kakaoProperties.apiKey();
        assertEquals(env.getProperty(apiKeyPrefix + "rest-api"), apiKeyProperties.restApi());
        assertEquals(env.getProperty(apiKeyPrefix + "admin"), apiKeyProperties.admin());

        String apiPathPrefix = prefix + "api-path.";
        KakaoProperties.KakaoApiPathProperties apiPathProperties = kakaoProperties.apiPath();
        assertEquals(env.getProperty(apiPathPrefix + "get-auth-code"), apiPathProperties.getAuthCode());
        assertEquals(env.getProperty(apiPathPrefix + "get-access-token"), apiPathProperties.getAccessToken());
        assertEquals(env.getProperty(apiPathPrefix + "get-access-token-info"), apiPathProperties.getAccessTokenInfo());
        assertEquals(env.getProperty(apiPathPrefix + "get-userinfo-by-access-token"), apiPathProperties.getUserInfoByAccessToken());

        String userPropertyPrefix = prefix + "user-property.";
        KakaoProperties.KakaoUserPropertyProperties userPropertyProperties = kakaoProperties.userProperty();
        assertEquals(env.getProperty(userPropertyPrefix + "profile"), userPropertyProperties.profile());
        assertEquals(env.getProperty(userPropertyPrefix + "name"), userPropertyProperties.name());
        assertEquals(env.getProperty(userPropertyPrefix + "email"), userPropertyProperties.email());
        assertEquals(env.getProperty(userPropertyPrefix + "age-range"), userPropertyProperties.ageRange());
        assertEquals(env.getProperty(userPropertyPrefix + "birthday"), userPropertyProperties.birthday());
        assertEquals(env.getProperty(userPropertyPrefix + "gender"), userPropertyProperties.gender());
    }

}