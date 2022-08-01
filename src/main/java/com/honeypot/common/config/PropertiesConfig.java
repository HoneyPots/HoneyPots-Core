package com.honeypot.common.config;

import com.honeypot.common.model.properties.JwtProperties;
import com.honeypot.common.model.properties.KakaoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({KakaoProperties.class, JwtProperties.class})
public class PropertiesConfig {
}
