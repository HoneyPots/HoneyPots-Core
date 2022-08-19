package com.honeypot.domain.member.api;

import com.honeypot.common.utils.SecurityUtils;
import com.honeypot.domain.auth.service.contracts.AuthTokenManagerService;
import com.honeypot.domain.post.dto.NormalPostDto;
import com.honeypot.domain.post.entity.enums.PostType;
import com.honeypot.domain.post.service.NormalPostService;
import com.honeypot.domain.post.service.PostCrudServiceFactory;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MyPageApi.class)
@ActiveProfiles("test")
@MockBean(classes = {JpaMetamodelMappingContext.class, AuthTokenManagerService.class})
@ExtendWith(MockitoExtension.class)
class MyPageApiTest {

    @MockBean
    private PostCrudServiceFactory postCrudServiceFactory;

    @Autowired
    private MockMvc mockMvc;

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
    void pageList_NormalPost_200_OK() throws Exception {
        // Arrange
        Long memberId = 1L;

        int page = 0;
        int size = 10;
        int totalCount = 30;

        String sortProperty = "createdAt";
        String sortDirection = "desc";
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(sortProperty)));
        PostType postType = PostType.NORMAL;

        List<NormalPostDto> list = createNormalPosts(page, size, totalCount);
        Page<NormalPostDto> pageResult = new PageImpl<>(list, pageable, totalCount);

        NormalPostService service = mock(NormalPostService.class);
        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(memberId));
        when(postCrudServiceFactory.getService(postType)).thenReturn(service);
        when(service.pageListByMemberId(pageable, memberId)).thenReturn(pageResult);

        // Act
        ResultActions actions = mockMvc.perform(get("/api/members/posts")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .queryParam("postType", postType.name())
                .queryParam("page", String.valueOf(page))
                .queryParam("size", String.valueOf(size))
                .queryParam("sort", sortProperty + "," + sortDirection)
        );

        // Assert
        actions.andExpect(status().isOk()).andDo(print());
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

}