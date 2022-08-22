package com.honeypot.domain.post.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeypot.common.utils.SecurityUtils;
import com.honeypot.domain.auth.service.contracts.AuthTokenManagerService;
import com.honeypot.domain.member.dto.WriterDto;
import com.honeypot.domain.post.dto.NormalPostDto;
import com.honeypot.domain.post.dto.NormalPostUploadRequest;
import com.honeypot.domain.post.service.NormalPostService;
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

@WebMvcTest(NormalPostApi.class)
@ActiveProfiles("test")
@MockBean(classes = {JpaMetamodelMappingContext.class, AuthTokenManagerService.class})
@ExtendWith(MockitoExtension.class)
class NormalPostApiTest {

    @MockBean
    private NormalPostService normalPostService;

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
    void pageList_200_OK() throws Exception {
        // Arrange
        int page = 0;
        int size = 10;
        String sortProperty = "createdAt";
        String sortDirection = "desc";
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(sortProperty)));
        Long memberId = 1L;

        int totalCount = 30;
        List<NormalPostDto> list = createNormalPosts(page, size, totalCount);
        Page<NormalPostDto> pageResult = new PageImpl<>(list, pageable, totalCount);

        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(memberId));
        when(normalPostService.pageList(pageable, memberId)).thenReturn(pageResult);

        // Act
        ResultActions actions = mockMvc.perform(get("/api/posts/normal")
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
        ResultActions actions = mockMvc.perform(get("/api/posts/normal")
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
        when(normalPostService.find(postId, memberId))
                .thenReturn(createNormalPost(postId, new WriterDto(memberId, "nickname")));

        // Act
        ResultActions actions = mockMvc.perform(get("/api/posts/normal/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
        );

        // Assert
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("postId").value(postId))
                .andExpect(jsonPath("title").exists())
                .andExpect(jsonPath("content").exists())
                .andExpect(jsonPath("writer").exists())
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

        NormalPostUploadRequest request = NormalPostUploadRequest.builder()
                .title("title")
                .content("content")
                .build();

        NormalPostUploadRequest request2 = NormalPostUploadRequest.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .writerId(memberId)
                .build();

        NormalPostDto created = createNormalPost(1L, new WriterDto(memberId, "nickname"));
        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(memberId));
        when(normalPostService.upload(request2)).thenReturn(created);

        // Act
        ResultActions actions = mockMvc.perform(post("/api/posts/normal")
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
                .andExpect(jsonPath("commentCount").exists())
                .andExpect(jsonPath("likeReactionCount").exists())
                .andExpect(jsonPath("uploadedAt").exists())
                .andExpect(jsonPath("lastModifiedAt").exists())
                .andDo(print());
    }

    @Test
    void upload_400_BadRequest() throws Exception {
        // Arrange
        NormalPostUploadRequest request = NormalPostUploadRequest.builder()
                .title("  ")
                .content("content")
                .build();

        // Act
        ResultActions actions = mockMvc.perform(post("/api/posts/normal")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .content(objectMapper.writeValueAsString(request))
        );

        // Assert
        actions.andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void upload_401_Unauthorized() throws Exception {
        // Arrange
        NormalPostUploadRequest request = NormalPostUploadRequest.builder()
                .title("title")
                .content("content")
                .build();

        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.empty());

        // Act
        ResultActions actions = mockMvc.perform(post("/api/posts/normal")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .content(objectMapper.writeValueAsString(request))
        );

        // Assert
        actions.andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    void update_204_NoContent() throws Exception {
        // Arrange
        Long postId = 1L;
        Long memberId = 1L;

        NormalPostUploadRequest request = NormalPostUploadRequest.builder()
                .title("title_modify")
                .content("content_modify")
                .build();

        NormalPostUploadRequest request2 = NormalPostUploadRequest.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .writerId(memberId)
                .build();

        NormalPostDto existed = createNormalPost(postId, new WriterDto(memberId, "nickname"));
        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(memberId));
        when(normalPostService.update(postId, request2)).thenReturn(existed);

        // Act
        ResultActions actions = mockMvc.perform(put("/api/posts/normal/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .content(objectMapper.writeValueAsString(request))
        );

        // Assert
        actions.andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void update_400_BadRequest() throws Exception {
        // Arrange
        NormalPostUploadRequest request = NormalPostUploadRequest.builder()
                .title("             ")
                .content(null)
                .build();

        // Act
        ResultActions actions = mockMvc.perform(put("/api/posts/normal/{postId}", 1L)
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
        NormalPostUploadRequest request = NormalPostUploadRequest.builder()
                .title("title")
                .content("content")
                .build();

        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.empty());

        // Act
        ResultActions actions = mockMvc.perform(put("/api/posts/normal/{postId}", 1L)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .content(objectMapper.writeValueAsString(request))
        );

        // Assert
        actions.andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    void delete_204_NoContent() throws Exception {
        // Arrange
        Long postId = 1L;
        Long memberId = 1L;

        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(memberId));
        doNothing().when(normalPostService).delete(postId, memberId);

        // Act
        ResultActions actions = mockMvc.perform(delete("/api/posts/normal/{postId}", postId)
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
        ResultActions actions = mockMvc.perform(delete("/api/posts/normal/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
        );

        // Assert
        actions.andExpect(status().isUnauthorized()).andDo(print());
    }

    private List<NormalPostDto> createNormalPosts(int page, int size, int totalCount) {
        int start = page * size;
        int end = (page + 1) * size;
        end = Math.min(end, totalCount);

        List<NormalPostDto> result = new ArrayList<>();
        for (int i = start; i < end; i++) {
            NormalPostDto dto = NormalPostDto.builder()
                    .postId(i + 1L)
                    .build();
            result.add(dto);
        }

        return result;
    }

    private NormalPostDto createNormalPost(Long postId, WriterDto writerDto) {
        LocalDateTime now = LocalDateTime.now();
        return NormalPostDto.builder()
                .postId(postId)
                .title("title")
                .content("content")
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