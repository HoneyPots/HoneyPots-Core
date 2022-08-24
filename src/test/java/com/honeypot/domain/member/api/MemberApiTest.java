package com.honeypot.domain.member.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeypot.common.utils.SecurityUtils;
import com.honeypot.domain.auth.service.contracts.AuthTokenManagerService;
import com.honeypot.domain.member.dto.NicknameModifyRequest;
import com.honeypot.domain.member.service.MemberNicknameModifyService;
import com.honeypot.domain.member.service.MemberWithdrawService;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberApi.class)
@ActiveProfiles("test")
@MockBean(classes = {JpaMetamodelMappingContext.class, AuthTokenManagerService.class})
@ExtendWith(MockitoExtension.class)
class MemberApiTest {

    @MockBean
    private MemberNicknameModifyService memberNicknameModifyService;

    @MockBean
    private MemberWithdrawService memberWithdrawService;

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
    void getNicknameProfile_200_OK() throws Exception {
        // Arrange
        String nickname = "admin";
        when(memberNicknameModifyService.isAvailableNickname(nickname)).thenReturn(false);

        // Act
        ResultActions actions = mockMvc.perform(get("/api/members/profile")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("nickname", nickname)
        );

        // Assert
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("nickname").exists())
                .andReturn();
    }

    @Test
    void getNicknameProfile_404_NotFound() throws Exception {
        // Arrange
        String nickname = "admin";
        when(memberNicknameModifyService.isAvailableNickname(nickname)).thenReturn(true);

        // Act
        ResultActions actions = mockMvc.perform(get("/api/members/profile")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("nickname", nickname)
        );

        // Assert
        actions.andExpect(status().isNotFound()).andReturn();
    }

    @Test
    void changeNickname_Success_204_NoContent() throws Exception {
        // Arrange
        Long memberId = 1L;
        NicknameModifyRequest request = createNicknameModifyRequest("nickname");

        request.setMemberId(memberId);
        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(memberId));
        when(memberNicknameModifyService.changeNickname(request)).thenReturn(true);

        // Act
        ResultActions actions = mockMvc.perform(patch("/api/members/{memberId}/profile/nickname", memberId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer test")
                .content(objectMapper.writeValueAsString(request))
        );

        // Assert
        actions.andExpect(status().isNoContent()).andReturn();
    }

    @Test
    void changeNickname_Fail_401_Unauthorized() throws Exception {
        // Arrange
        Long memberId = 1L;
        NicknameModifyRequest request = createNicknameModifyRequest("nickname");

        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(2L));

        // Act
        ResultActions actions = mockMvc.perform(patch("/api/members/{memberId}/profile/nickname", memberId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .content(objectMapper.writeValueAsString(request))
        );

        // Assert
        actions.andExpect(status().isForbidden())
                .andExpect(jsonPath("code").value("HAE002"))
                .andReturn();
    }

    @Test
    void changeNickname_Fail_409_Conflict() throws Exception {
        // Arrange
        Long memberId = 1L;
        NicknameModifyRequest request = createNicknameModifyRequest("nicknameTest");

        request.setMemberId(memberId);
        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(memberId));
        when(memberNicknameModifyService.changeNickname(request)).thenReturn(false);

        // Act
        ResultActions actions = mockMvc.perform(patch("/api/members/{memberId}/profile/nickname", memberId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer test")
                .content(objectMapper.writeValueAsString(request))
        );

        // Assert
        actions.andExpect(status().isConflict())
                .andExpect(jsonPath("nickname").exists());
    }

    @Test
    void deleteMember_Success_204_NoContent() throws Exception {
        // Arrange
        Long memberId = 1L;

        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(memberId));
        when(memberWithdrawService.withdraw(memberId)).thenReturn(true);

        // Act
        ResultActions actions = mockMvc.perform(delete("/api/members/{memberId}", memberId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer test")
                .header(HttpHeaders.COOKIE, "refreshToken=refreshToken;")
        );

        // Assert
        actions.andExpect(status().isNoContent())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andReturn();
    }

    @Test
    void deleteMember_Fail_InvalidAuthorizationException() throws Exception {
        // Arrange
        Long memberId = 1L;

        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(2L));

        // Act
        ResultActions actions = mockMvc.perform(delete("/api/members/{memberId}", memberId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer test")
                .header(HttpHeaders.COOKIE, "refreshToken=refreshToken;")
        );

        // Assert
        actions.andExpect(status().isForbidden())
                .andExpect(jsonPath("code").value("HAE002"))
                .andReturn();
    }

    private NicknameModifyRequest createNicknameModifyRequest(String nickname) {
        return NicknameModifyRequest.builder()
                .nickname(nickname)
                .build();
    }

}