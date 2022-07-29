package com.honeypot.common.model.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.kakao")
public record KakaoProperties(String clientSecret,
                              String redirectUrl,
                              KakaoApiKeyProperties apiKey,
                              KakaoApiPathProperties apiPath,
                              KakaoUserPropertyProperties userProperty) {


    public record KakaoApiKeyProperties(String restApi,
                                        String admin) {
    }

    public record KakaoApiPathProperties(String getAuthCode,
                                         String getAccessToken,
                                         String getAccessTokenInfo,
                                         String getUserInfoByAccessToken) {
    }

    public record KakaoUserPropertyProperties(String profile,
                                              String name,
                                              String email,
                                              String ageRange,
                                              String birthday,
                                              String gender) {
    }
}
