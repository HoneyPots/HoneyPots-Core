package com.honeypot.domain.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeypot.common.utils.SecurityUtils;
import com.honeypot.domain.auth.service.contracts.AuthTokenManagerService;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportApi.class)
@ActiveProfiles("test")
@MockBean(classes = {JpaMetamodelMappingContext.class, ReportService.class, AuthTokenManagerService.class})
@ExtendWith(MockitoExtension.class)
class ReportApiTest {

    @Mock
    private ReportService reportService;

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
        reportService = mock(ReportService.class);
        ReportApi reportApi = new ReportApi(reportService);
        mockMvc = MockMvcBuilders.standaloneSetup(reportApi).build();
    }

    @AfterAll
    public static void teardown() {
        mockStatic.close();
    }

    @Test
    void upload() throws Exception {
        // Arrange
        String token = "Bearer test";
        ReportTarget target = ReportTarget.POST;
        Long targetId = 1L;
        String reason = "사유";
        Long reporterId = 2L;

        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(reporterId));

        ReportUploadRequest uploadRequest = ReportUploadRequest.builder()
                .target(target)
                .targetId(targetId)
                .reason(reason)
                .build();

        String requestBody = objectMapper.writeValueAsString(uploadRequest);
        uploadRequest.setReporterId(reporterId);

        LocalDateTime createdAt = LocalDateTime.now();
        ReportDto uploaded = ReportDto.builder()
                .reportId(1L)
                .target(target)
                .targetId(targetId)
                .reason(reason)
                .reporterId(reporterId)
                .createdAt(createdAt)
                .lastModifiedAt(createdAt)
                .build();

        when(reportService.upload(uploadRequest)).thenReturn(uploaded);

        // Act
        ResultActions actions = mockMvc.perform(post("/api/reports")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(requestBody)
        );

        // Assert
        actions.andExpect(status().isCreated())
                .andExpect(jsonPath("reportId").exists())
                .andExpect(jsonPath("target").exists())
                .andExpect(jsonPath("targetId").exists())
                .andExpect(jsonPath("reason").exists())
                .andExpect(jsonPath("reporterId").exists())
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("lastModifiedAt").exists())
                .andReturn();
    }

}