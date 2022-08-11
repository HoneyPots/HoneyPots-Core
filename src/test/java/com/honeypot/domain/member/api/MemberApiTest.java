package com.honeypot.domain.member.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeypot.common.model.exceptions.InvalidAuthorizationException;
import com.honeypot.common.utils.SecurityUtils;
import com.honeypot.domain.auth.service.contracts.AuthTokenManagerService;
import com.honeypot.domain.member.dto.NicknameModifyRequest;
import com.honeypot.domain.member.service.MemberNicknameModifyService;
import com.honeypot.domain.member.service.MemberWithdrawService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberApi.class)
@ActiveProfiles("test")
@MockBean(classes = {JpaMetamodelMappingContext.class, AuthTokenManagerService.class})
@ExtendWith(MockitoExtension.class)
class MemberApiTest {

    @MockBean
    private MemberNicknameModifyService memberNicknameModifyService;

    @MockBean
    private MemberWithdrawService memberWithdrawService;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static MockedStatic<SecurityUtils> mockStatic;

    @BeforeAll
    public static void setup() {
        mockStatic = mockStatic(SecurityUtils.class);
    }

    @BeforeEach
    public void before() {
        memberNicknameModifyService = mock(MemberNicknameModifyService.class);
        MemberApi memberApi = new MemberApi(memberNicknameModifyService, memberWithdrawService, objectMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(memberApi).build();
    }

    @AfterAll
    public static void teardown() {
        mockStatic.close();
    }

    @Test
    void changeNickname_Success_204_NoContent() throws Exception {
        // Arrange
        String token = "Bearer test";
        Long memberId = 1L;
        NicknameModifyRequest request = createNicknameModifyRequest("nickname");

        String requestBody = objectMapper.writeValueAsString(request);

        request.setMemberId(memberId);
        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(memberId));
        when(memberNicknameModifyService.changeNickname(request)).thenReturn(true);

        // Act
        ResultActions actions = mockMvc.perform(patch("/api/members/" + memberId + "/profile/nickname")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", token)
                .content(requestBody)
        );

        // Assert
        actions.andExpect(status().isNoContent()).andReturn();
    }

    @Test
    void changeNickname_Fail_409_Conflict() throws Exception {
        // Arrange
        String token = "Bearer test";
        Long memberId = 1L;
        NicknameModifyRequest request = createNicknameModifyRequest("nicknameTest");

        String requestBody = objectMapper.writeValueAsString(request);

        request.setMemberId(memberId);
        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(memberId));
        when(memberNicknameModifyService.changeNickname(request)).thenReturn(false);

        // Act
        ResultActions actions = mockMvc.perform(patch("/api/members/" + memberId + "/profile/nickname")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", token)
                .content(requestBody)
        );

        // Assert
        actions.andExpect(status().isConflict())
                .andExpect(jsonPath("nickname").exists());
    }

    @Test
    void deleteMember_Success_204_NoContent() throws Exception {
        // Arrange
        String token = "Bearer test";
        Long memberId = 1L;

        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(memberId));
        when(memberWithdrawService.withdraw(memberId)).thenReturn(true);

        // Act
        ResultActions actions = mockMvc.perform(delete("/api/members/" + memberId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", token)
        );

        // Assert
        actions.andExpect(status().isNoContent()).andReturn();
    }

    @Test
    void deleteMember_Fail_InvalidAuthorizationException() throws Exception {
        // Arrange
        String token = "Bearer test";
        Long memberId = 1L;

        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(2L));
        when(memberWithdrawService.withdraw(memberId)).thenReturn(true);

        // Act & Assert
        assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(delete("/api/members/" + memberId)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", token)
            );
        });
    }

    private NicknameModifyRequest createNicknameModifyRequest(String nickname) {
        return NicknameModifyRequest.builder()
                .nickname(nickname)
                .build();
    }

}