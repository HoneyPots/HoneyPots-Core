package com.honeypot.common.model.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.kakao")
public record KakaoProperties(String clientSecret,
                              String redirectUrl,
                              kakaoApiKeyProperties apiKey,
                              kakaoApiPathProperties apiPath) {

    public record kakaoApiKeyProperties(String restApi,
                                        String admin) {
    }

    public record kakaoApiPathProperties(String getAuthCode,
                                         String getAccessToken,
                                         String getAccessTokenInfo) {
    }
}
