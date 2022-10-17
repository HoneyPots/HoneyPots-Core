package com.honeypot.domain.auth.repository;

import com.amazonaws.HttpMethod;
import com.honeypot.common.config.PropertiesConfig;
import com.honeypot.common.config.WebClientConfig;
import com.honeypot.common.model.properties.KakaoProperties;
import com.honeypot.domain.auth.dto.kakao.KakaoTokenInfo;
import com.honeypot.domain.auth.dto.kakao.KakaoTokenIssuance;
import com.honeypot.domain.auth.dto.kakao.KakaoUserInfo;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest(classes = {PropertiesConfig.class, WebClientConfig.class})
@ExtendWith({SpringExtension.class, MockitoExtension.class})
class KakaoAuthRepositoryTest {

    @Autowired
    private KakaoProperties kakaoProperties;

    @Autowired
    private WebClient webClient;

    private KakaoAuthRepository kakaoAuthRepository;

    private MockWebServer server;

    private String baseUrl;

    @BeforeEach
    public void before() throws IOException {
        kakaoProperties = spy(kakaoProperties);
        kakaoAuthRepository = new KakaoAuthRepository(webClient, kakaoProperties);
        server = new MockWebServer();
        server.start();
        baseUrl = "http://localhost:" + server.getPort();
    }

    @AfterEach
    public void after() throws IOException {
        server.shutdown();
    }

    @Test
    void getAccessToken_Success() throws IOException, InterruptedException, URISyntaxException {
        // Arrange
        KakaoProperties.KakaoApiPathProperties mock = mock(KakaoProperties.KakaoApiPathProperties.class);
        when(kakaoProperties.apiPath()).thenReturn(mock);
        when(mock.getAccessToken()).thenReturn(baseUrl + "/oauth/token");

        String authorizationCode = "authCode";
        Path responseFile = Paths.get(getClass().getResource("/response/kakao-get-access-token-success.json").toURI());
        String responseBody = Files.readString(responseFile);

        MockResponse response = new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                .setResponseCode(200)
                .setBody(responseBody);

        server.enqueue(response);

        // Act
        KakaoTokenIssuance result = kakaoAuthRepository.getAccessToken(authorizationCode).block();

        // Assert
        RecordedRequest request = server.takeRequest();

        assertEquals(HttpMethod.POST.name(), request.getMethod());
        assertEquals(MediaType.APPLICATION_FORM_URLENCODED_VALUE, request.getHeader(HttpHeaders.CONTENT_TYPE));

        assertNotNull(request.getRequestUrl().queryParameter("grant_type"));
        assertNotNull(request.getRequestUrl().queryParameter("client_id"));
        assertNotNull(request.getRequestUrl().queryParameter("redirect_uri"));
        assertNotNull(request.getRequestUrl().queryParameter("code"));
        assertNotNull(request.getRequestUrl().queryParameter("client_secret"));

        assertEquals("bearer", result.getTokenType());
        assertEquals("ACCESS_TOKEN", result.getAccessToken());
        assertEquals("REFRESH_TOKEN", result.getRefreshToken());
        assertTrue(result.getExpiresIn() > 0);
        assertTrue(result.getRefreshTokenExpiresIn() > 0);
    }

    @Test
    void getTokenInfo() throws IOException, InterruptedException, URISyntaxException {
        // Arrange
        KakaoProperties.KakaoApiPathProperties mock = mock(KakaoProperties.KakaoApiPathProperties.class);
        when(kakaoProperties.apiPath()).thenReturn(mock);
        when(mock.getAccessTokenInfo()).thenReturn(baseUrl + "/user/access_token_info");

        String accessToken = "accessToken";
        Path responseFile = Paths.get(getClass().getResource("/response/kakao-get-access-token-info-success.json").toURI());
        String responseBody = Files.readString(responseFile);

        MockResponse response = new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                .setResponseCode(200)
                .setBody(responseBody);

        server.enqueue(response);

        // Act
        KakaoTokenInfo result = kakaoAuthRepository.getTokenInfo(accessToken).block();

        // Assert
        RecordedRequest request = server.takeRequest();

        assertEquals(HttpMethod.GET.name(), request.getMethod());
        assertNotNull(request.getHeader(HttpHeaders.AUTHORIZATION));

        assertNotEquals(0, result.getId());
        assertNotEquals(0, result.getExpiresIn());
        assertNotEquals(0, result.getAppId());
    }

    @Test
    void getUserInfoByAccessToken() throws IOException, InterruptedException, URISyntaxException {
        // Arrange
        KakaoProperties.KakaoApiPathProperties mock = mock(KakaoProperties.KakaoApiPathProperties.class);
        when(kakaoProperties.apiPath()).thenReturn(mock);
        when(mock.getUserInfoByAccessToken()).thenReturn(baseUrl + "/v2/user/me");

        String accessToken = "accessToken";
        Path responseFile = Paths.get(getClass().getResource("/response/kakao-get-user-info-by-access-token-success.json").toURI());
        String responseBody = Files.readString(responseFile);

        MockResponse response = new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                .setResponseCode(200)
                .setBody(responseBody);

        server.enqueue(response);

        // Act
        KakaoUserInfo result = kakaoAuthRepository.getUserInfoByAccessToken(accessToken).block();

        // Assert
        RecordedRequest request = server.takeRequest();

        assertEquals(HttpMethod.GET.name(), request.getMethod());
        assertNotNull(request.getHeader(HttpHeaders.AUTHORIZATION));

        assertNotNull(result);
    }

}