package com.honeypot.domain.post.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeypot.common.utils.SecurityUtils;
import com.honeypot.domain.auth.service.contracts.AuthTokenManagerService;
import com.honeypot.domain.member.dto.WriterDto;
import com.honeypot.domain.post.dto.GroupBuyingModifyRequest;
import com.honeypot.domain.post.dto.GroupBuyingPostDto;
import com.honeypot.domain.post.dto.GroupBuyingPostUploadRequest;
import com.honeypot.domain.post.entity.enums.GroupBuyingStatus;
import com.honeypot.domain.post.service.GroupBuyingPostService;
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
import org.springframework.data.domain.*;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GroupBuyingPostApi.class)
@ActiveProfiles("test")
@MockBean(classes = {JpaMetamodelMappingContext.class, AuthTokenManagerService.class})
@ExtendWith(MockitoExtension.class)
class GroupBuyingPostApiTest {

    @MockBean
    private GroupBuyingPostService groupBuyingPostService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static MockedStatic<SecurityUtils> securityUtilsMockedStatic;

    @BeforeAll
    public static void setup() {
        securityUtilsMockedStatic = mockStatic(SecurityUtils.class);
    }

    @AfterAll
    public static void teardown() {
        securityUtilsMockedStatic.close();
    }

    @Test
    void pageList_200_OK() throws Exception {
        // Arrange
        int page = 0;
        int size = 10;
        String sortProperty = "createdAt";
        String sortDirection = "desc";
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(sortProperty)));
        Long memberId = 1L;

        int totalCount = 30;
        List<GroupBuyingPostDto> list = createGroupBuyingPosts(page, size, totalCount);
        Page<GroupBuyingPostDto> pageResult = new PageImpl<>(list, pageable, totalCount);

        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(memberId));
        when(groupBuyingPostService.pageList(pageable, memberId)).thenReturn(pageResult);

        // Act
        ResultActions actions = mockMvc.perform(get("/api/posts/group-buying")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .queryParam("page", String.valueOf(page))
                .queryParam("size", String.valueOf(size))
                .queryParam("sort", sortProperty + "," + sortDirection)
        );

        // Assert
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("content").isArray())
                .andExpect(jsonPath("pageable").exists())
                .andExpect(jsonPath("last").exists())
                .andExpect(jsonPath("totalElements").exists())
                .andExpect(jsonPath("totalPages").exists())
                .andExpect(jsonPath("size").exists())
                .andExpect(jsonPath("number").exists())
                .andExpect(jsonPath("sort").exists())
                .andExpect(jsonPath("first").exists())
                .andExpect(jsonPath("numberOfElements").exists())
                .andExpect(jsonPath("empty").exists())
                .andDo(print());
    }

    @Test
    void pageList_400_BadRequest() throws Exception {
        // Arrange
        int page = 0;
        int size = 10;
        String sortProperty = "invalidSortProperty";
        String sortDirection = "desc";

        // Act
        ResultActions actions = mockMvc.perform(get("/api/posts/group-buying")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .queryParam("page", String.valueOf(page))
                .queryParam("size", String.valueOf(size))
                .queryParam("sort", sortProperty + "," + sortDirection)
        );

        // Assert
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value("HRE001"))
                .andDo(print());
    }

    @Test
    void read_200_OK() throws Exception {
        // Arrange
        Long postId = 1L;
        Long memberId = 1L;

        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(memberId));
        when(groupBuyingPostService.find(postId, memberId))
                .thenReturn(createGroupBuyingPost(postId, new WriterDto(memberId, "nickname")));

        // Act
        ResultActions actions = mockMvc.perform(get("/api/posts/group-buying/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
        );

        // Assert
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("postId").value(postId))
                .andExpect(jsonPath("title").exists())
                .andExpect(jsonPath("content").exists())
                .andExpect(jsonPath("writer").exists())
                .andExpect(jsonPath("category").exists())
                .andExpect(jsonPath("groupBuyingStatus").exists())
                .andExpect(jsonPath("chatRoomLink").exists())
                .andExpect(jsonPath("deadline").exists())
                .andExpect(jsonPath("commentCount").exists())
                .andExpect(jsonPath("likeReactionCount").exists())
                .andExpect(jsonPath("uploadedAt").exists())
                .andExpect(jsonPath("lastModifiedAt").exists())
                .andDo(print());
    }

    @Test
    void upload_201_Created() throws Exception {
        // Arrange
        Long memberId = 1L;

        GroupBuyingPostUploadRequest request = GroupBuyingPostUploadRequest.builder()
                .title("title")
                .content("content")
                .category("category")
                .groupBuyingStatus(GroupBuyingStatus.ONGOING.name())
                .chatRoomLink("https://open.kakao/example")
                .deadline(LocalDateTime.now().plusHours(3))
                .build();

        GroupBuyingPostUploadRequest request2 = GroupBuyingPostUploadRequest.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .groupBuyingStatus(request.getGroupBuyingStatus())
                .chatRoomLink(request.getChatRoomLink())
                .deadline(request.getDeadline())
                .writerId(memberId)
                .build();

        GroupBuyingPostDto created = createGroupBuyingPost(1L, new WriterDto(memberId, "nickname"));
        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(memberId));
        when(groupBuyingPostService.upload(request2)).thenReturn(created);

        // Act
        ResultActions actions = mockMvc.perform(post("/api/posts/group-buying")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .content(objectMapper.writeValueAsString(request))
        );

        // Assert
        actions.andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("postId").value(created.getPostId()))
                .andExpect(jsonPath("title").exists())
                .andExpect(jsonPath("content").exists())
                .andExpect(jsonPath("writer").exists())
                .andExpect(jsonPath("category").exists())
                .andExpect(jsonPath("groupBuyingStatus").exists())
                .andExpect(jsonPath("chatRoomLink").exists())
                .andExpect(jsonPath("deadline").exists())
                .andExpect(jsonPath("commentCount").exists())
                .andExpect(jsonPath("likeReactionCount").exists())
                .andExpect(jsonPath("uploadedAt").exists())
                .andExpect(jsonPath("lastModifiedAt").exists())
                .andDo(print());
    }

    @Test
    void upload_400_BadRequest() throws Exception {
        // Arrange
        GroupBuyingPostUploadRequest request = GroupBuyingPostUploadRequest.builder()
                .title("dsf")
                .content("  d ")
                .groupBuyingStatus(GroupBuyingStatus.ONGOING.name())
                .chatRoomLink(null)
                .deadline(LocalDateTime.now().plusHours(3))
                .build();

        // Act
        ResultActions actions = mockMvc.perform(post("/api/posts/group-buying")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .content(objectMapper.writeValueAsString(request))
        );

        // Assert
        actions.andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    void upload_401_Unauthorized() throws Exception {
        // Arrange
        GroupBuyingPostUploadRequest request = GroupBuyingPostUploadRequest.builder()
                .title("dsf")
                .content("  d ")
                .category("한식")
                .groupBuyingStatus(GroupBuyingStatus.ONGOING.name())
                .chatRoomLink("example")
                .deadline(LocalDateTime.now().plusHours(3))
                .build();

        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.empty());

        // Act
        ResultActions actions = mockMvc.perform(post("/api/posts/group-buying")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .content(objectMapper.writeValueAsString(request))
        );

        // Assert
        actions.andExpect(status().isUnauthorized()).andDo(print());
    }

    @Test
    void update_204_NoContent() throws Exception {
        // Arrange
        Long postId = 1L;
        Long memberId = 1L;

        GroupBuyingPostUploadRequest request = GroupBuyingPostUploadRequest.builder()
                .title("title_modify")
                .content("content_modify")
                .category("category")
                .groupBuyingStatus(GroupBuyingStatus.ONGOING.name())
                .chatRoomLink("example")
                .deadline(LocalDateTime.now().plusHours(3))
                .build();

        GroupBuyingPostUploadRequest request2 = GroupBuyingPostUploadRequest.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .groupBuyingStatus(request.getGroupBuyingStatus())
                .chatRoomLink(request.getChatRoomLink())
                .deadline(request.getDeadline())
                .writerId(memberId)
                .build();

        GroupBuyingPostDto existed = createGroupBuyingPost(postId, new WriterDto(memberId, "nickname"));
        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(memberId));
        when(groupBuyingPostService.update(postId, request2)).thenReturn(existed);

        // Act
        ResultActions actions = mockMvc.perform(put("/api/posts/group-buying/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .content(objectMapper.writeValueAsString(request))
        );

        // Assert
        actions.andExpect(status().isNoContent()).andDo(print());
    }

    @Test
    void update_400_BadRequest() throws Exception {
        // Arrange
        GroupBuyingPostUploadRequest request = GroupBuyingPostUploadRequest.builder()
                .title("             ")
                .content(null)
                .build();

        // Act
        ResultActions actions = mockMvc.perform(put("/api/posts/group-buying/{postId}", 1L)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .content(objectMapper.writeValueAsString(request))
        );

        // Assert
        actions.andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void update_401_Unauthorized() throws Exception {
        // Arrange
        GroupBuyingPostUploadRequest request = GroupBuyingPostUploadRequest.builder()
                .title("title")
                .content("content")
                .category("한식")
                .groupBuyingStatus(GroupBuyingStatus.ONGOING.name())
                .chatRoomLink("example")
                .deadline(LocalDateTime.now().plusHours(3))
                .build();

        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.empty());

        // Act
        ResultActions actions = mockMvc.perform(put("/api/posts/group-buying/{postId}", 1L)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .content(objectMapper.writeValueAsString(request))
        );

        // Assert
        actions.andExpect(status().isUnauthorized()).andDo(print());
    }

    @Test
    void updateGroupBuyingStatus_204_NoContent() throws Exception {
        // Arrange
        Long postId = 1L;
        Long memberId = 1L;
        GroupBuyingModifyRequest request = GroupBuyingModifyRequest.builder()
                .groupBuyingStatus(GroupBuyingStatus.HOLD.name())
                .build();

        GroupBuyingModifyRequest request2 = GroupBuyingModifyRequest.builder()
                .groupBuyingStatus(request.getGroupBuyingStatus())
                .writerId(memberId)
                .build();

        GroupBuyingPostDto updated = createGroupBuyingPost(postId, new WriterDto(memberId, "nickname"));
        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(memberId));
        when(groupBuyingPostService.updateTradeStatus(postId, request2)).thenReturn(updated);

        // Act
        ResultActions actions = mockMvc.perform(patch("/api/posts/group-buying/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .content(objectMapper.writeValueAsString(request))
        );

        // Assert
        actions.andExpect(status().isNoContent()).andDo(print());
    }

    @Test
    void updateGroupBuyingStatus_400_BadRequest() throws Exception {
        // Arrange
        Long postId = 1L;
        GroupBuyingModifyRequest request = GroupBuyingModifyRequest.builder()
                .groupBuyingStatus("invalid")
                .build();

        // Act
        ResultActions actions = mockMvc.perform(patch("/api/posts/group-buying/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .content(objectMapper.writeValueAsString(request))
        );

        // Assert
        actions.andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    void updateGroupBuyingStatus_401_Unauthorized() throws Exception {
        // Arrange
        Long postId = 1L;
        GroupBuyingModifyRequest request = GroupBuyingModifyRequest.builder()
                .groupBuyingStatus(GroupBuyingStatus.COMPLETE.name())
                .build();

        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.empty());

        // Act
        ResultActions actions = mockMvc.perform(patch("/api/posts/group-buying/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .content(objectMapper.writeValueAsString(request))
        );

        // Assert
        actions.andExpect(status().isUnauthorized()).andDo(print());
    }

    @Test
    void delete_204_NoContent() throws Exception {
        // Arrange
        Long postId = 1L;
        Long memberId = 1L;

        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(memberId));
        doNothing().when(groupBuyingPostService).delete(postId, memberId);

        // Act
        ResultActions actions = mockMvc.perform(delete("/api/posts/group-buying/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
        );

        // Assert
        actions.andExpect(status().isNoContent()).andDo(print());
    }

    @Test
    void delete_401_Unauthorized() throws Exception {
        // Arrange
        Long postId = 1L;

        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.empty());

        // Act
        ResultActions actions = mockMvc.perform(delete("/api/posts/group-buying/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
        );

        // Assert
        actions.andExpect(status().isUnauthorized()).andDo(print());
    }

    private List<GroupBuyingPostDto> createGroupBuyingPosts(int page, int size, int totalCount) {
        int start = page * size;
        int end = (page + 1) * size;
        end = Math.min(end, totalCount);

        List<GroupBuyingPostDto> result = new ArrayList<>();
        for (int i = start; i < end; i++) {
            GroupBuyingPostDto dto = GroupBuyingPostDto.builder()
                    .postId(i + 1L)
                    .build();
            result.add(dto);
        }

        return result;
    }

    private GroupBuyingPostDto createGroupBuyingPost(Long postId, WriterDto writerDto) {
        LocalDateTime now = LocalDateTime.now();
        return GroupBuyingPostDto.builder()
                .postId(postId)
                .title("title")
                .content("content")
                .category("한식")
                .groupBuyingStatus(GroupBuyingStatus.ONGOING)
                .chatRoomLink("https://open.kakao/example")
                .deadline(LocalDateTime.now().plusHours(3))
                .writer(writerDto)
                .commentCount(10L)
                .likeReactionCount(10L)
                .likeReactionId(1L)
                .isLiked(true)
                .uploadedAt(now)
                .lastModifiedAt(now)
                .build();
    }

}