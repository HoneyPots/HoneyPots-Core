package com.honeypot.domain.notification.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeypot.common.utils.SecurityUtils;
import com.honeypot.domain.auth.service.contracts.AuthTokenManagerService;
import com.honeypot.domain.notification.dto.CommentNotificationResource;
import com.honeypot.domain.notification.dto.PostNotificationResource;
import com.honeypot.domain.notification.dto.ReactionNotificationResource;
import com.honeypot.domain.notification.service.NotificationHistoryServiceImpl;
import com.honeypot.domain.post.entity.enums.PostType;
import com.honeypot.domain.reaction.entity.enums.ReactionType;
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

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(NotificationApi.class)
@ExtendWith(MockitoExtension.class)
@MockBean(classes = {JpaMetamodelMappingContext.class, AuthTokenManagerService.class})
class NotificationApiTest {

    @MockBean
    private NotificationHistoryServiceImpl notificationHistoryService;

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
    void getNotificationResource_CommentResource() throws Exception {
        // Arrange
        Long notificationId = 1L;
        CommentNotificationResource resource = CommentNotificationResource.builder()
                .postResource(
                        PostNotificationResource.builder()
                                .id(123L)
                                .writer("postWriter")
                                .type(PostType.NORMAL)
                                .build()
                )
                .commentId(4L)
                .commenter("commentWriter")
                .build();

        when(notificationHistoryService.findNotificationResourceById(notificationId)).thenReturn(resource);

        // Act
        ResultActions actions = mockMvc.perform(get("/api/notifications/{notificationId}", notificationId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
        );

        // Assert
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("postResource").exists())
                .andExpect(jsonPath("postResource.id").value(resource.getPostResource().getId()))
                .andExpect(jsonPath("postResource.type").value(resource.getPostResource().getType().name()))
                .andExpect(jsonPath("postResource.writer").value(resource.getPostResource().getWriter()))
                .andExpect(jsonPath("commentId").value(resource.getCommentId()))
                .andExpect(jsonPath("commenter").value(resource.getCommenter()))
                .andDo(print());
    }

    @Test
    void getNotificationResource_ReactionResource() throws Exception {
        // Arrange
        Long notificationId = 1L;
        ReactionNotificationResource resource = ReactionNotificationResource.builder()
                .postResource(
                        PostNotificationResource.builder()
                                .id(123L)
                                .writer("postWriter")
                                .type(PostType.NORMAL)
                                .build()
                )
                .reactionId(5L)
                .reactionType(ReactionType.LIKE)
                .reactor("commentWriter")
                .build();

        when(notificationHistoryService.findNotificationResourceById(notificationId)).thenReturn(resource);

        // Act
        ResultActions actions = mockMvc.perform(get("/api/notifications/{notificationId}", notificationId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
        );

        // Assert
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("postResource").exists())
                .andExpect(jsonPath("postResource.id").value(resource.getPostResource().getId()))
                .andExpect(jsonPath("postResource.type").value(resource.getPostResource().getType().name()))
                .andExpect(jsonPath("postResource.writer").value(resource.getPostResource().getWriter()))
                .andExpect(jsonPath("reactionId").value(resource.getReactionId()))
                .andExpect(jsonPath("reactionType").value(resource.getReactionType().name()))
                .andExpect(jsonPath("reactor").value(resource.getReactor()))
                .andDo(print());
    }

}