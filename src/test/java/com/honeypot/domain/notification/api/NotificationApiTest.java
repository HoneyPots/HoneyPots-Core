package com.honeypot.domain.notification.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeypot.common.utils.SecurityUtils;
import com.honeypot.domain.auth.service.contracts.AuthTokenManagerService;
import com.honeypot.domain.notification.entity.enums.ClientType;
import com.honeypot.domain.notification.dto.NotificationTokenDto;
import com.honeypot.domain.notification.service.NotificationTokenManageService;
import com.honeypot.domain.notification.dto.NotificationTokenUploadRequest;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(NotificationApi.class)
@ExtendWith(MockitoExtension.class)
@MockBean(classes = {JpaMetamodelMappingContext.class, AuthTokenManagerService.class})
class NotificationApiTest {

    @MockBean
    private NotificationTokenManageService notificationTokenManageService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static MockedStatic<SecurityUtils> mockStatic;

    @BeforeAll
    public static void setup() {
        mockStatic = mockStatic(SecurityUtils.class);
    }

    @AfterAll
    public static void teardown() {
        mockStatic.close();
    }

    @Test
    void saveNotificationToken_201_Created() throws Exception {
        // Arrange
        Long memberId = 1L;
        NotificationTokenUploadRequest request = NotificationTokenUploadRequest.builder()
                .deviceToken("notificationDeviceToken")
                .clientType(ClientType.WEB)
                .build();

        String reqBody = objectMapper.writeValueAsString(request);

        request.setMemberId(memberId);
        NotificationTokenDto created = NotificationTokenDto.builder()
                .notificationTokenId(1L)
                .memberId(memberId)
                .deviceToken(request.getDeviceToken())
                .clientType(request.getClientType())
                .build();

        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(memberId));
        when(notificationTokenManageService.save(request)).thenReturn(created);

        // Act
        ResultActions actions = mockMvc.perform(post("/api/notifications/tokens")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .content(reqBody)
        );

        // Assert
        actions.andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("notificationTokenId").value(created.getNotificationTokenId()))
                .andDo(print());
    }

    @Test
    void saveNotificationToken_400_BadRequest_TokenIsNull() throws Exception {
        // Arrange
        NotificationTokenUploadRequest request = NotificationTokenUploadRequest.builder()
                .clientType(ClientType.WEB)
                .build();

        // Act
        ResultActions actions = mockMvc.perform(post("/api/notifications/tokens")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .content(objectMapper.writeValueAsString(request))
        );

        // Assert
        actions.andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void saveNotificationToken_401_Unauthorized() throws Exception {
        // Arrange
        Long memberId = 1L;
        NotificationTokenUploadRequest request = NotificationTokenUploadRequest.builder()
                .deviceToken("notificationDeviceToken")
                .clientType(ClientType.WEB)
                .build();

        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.empty());

        // Act
        ResultActions actions = mockMvc.perform(post("/api/notifications/tokens")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .content(objectMapper.writeValueAsString(request))
        );

        // Assert
        actions.andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    void removeNotificationToken() throws Exception {
        // Arrange
        Long memberId = 1L;
        Long notificationTokenId = 12314112897L;

        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(memberId));
        doNothing().when(notificationTokenManageService).remove(memberId, notificationTokenId);

        // Act
        ResultActions actions = mockMvc.perform(delete("/api/notifications/tokens/{notificationTokenId}", notificationTokenId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
        );

        // Assert
        actions.andExpect(status().isNoContent())
                .andDo(print());
    }

}