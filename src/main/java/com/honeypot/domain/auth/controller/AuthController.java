package com.honeypot.domain.auth.controller;

import com.honeypot.common.model.properties.KakaoProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping("/view/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KakaoProperties kakaoProperties;

    @GetMapping("/login")
    public String login(Model model) {
        UriComponents kakaoUrl = UriComponentsBuilder
                .fromUriString(kakaoProperties.apiPath().getAuthCode())
                .queryParam("client_id", kakaoProperties.apiKey().restApi())
                .queryParam("redirect_uri", kakaoProperties.redirectUrl())
                .queryParam("response_type", "code")
                .build();

        model.addAttribute("kakaoUrl", kakaoUrl);

        return "login";
    }
}
