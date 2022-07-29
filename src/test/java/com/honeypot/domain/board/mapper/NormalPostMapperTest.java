package com.honeypot.domain.board.mapper;

import com.honeypot.domain.board.dto.NormalPostDto;
import com.honeypot.domain.board.dto.NormalPostUploadRequest;
import com.honeypot.domain.board.entity.Comment;
import com.honeypot.domain.board.entity.NormalPost;
import com.honeypot.domain.member.dto.WriterDto;
import com.honeypot.domain.member.entity.Member;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NormalPostMapperTest {

    private final NormalPostMapper mapper = Mappers.getMapper(NormalPostMapper.class);

    @Test
    void toEntity() {
        // Arrange
        LocalDateTime uploadedAt = LocalDateTime.of(2022, 7, 28, 22, 0, 0);
        NormalPostDto dto = NormalPostDto.builder()
                .postId(1)
                .title("title")
                .content("content")
                .writer(WriterDto.builder()
                        .id(1)
                        .nickname("nickname")
                        .build())
                .uploadedAt(uploadedAt)
                .lastModifiedAt(uploadedAt)
                .build();

        // Act
        NormalPost entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getPostId(), entity.getId());
        assertEquals(dto.getTitle(), entity.getTitle());
        assertEquals(dto.getContent(), entity.getContent());
        assertEquals(dto.getWriter().getId(), entity.getWriter().getId());
        assertEquals(dto.getWriter().getNickname(), entity.getWriter().getNickname());
        assertEquals(dto.getUploadedAt(), entity.getCreatedAt());
        assertEquals(dto.getLastModifiedAt(), entity.getLastModifiedAt());
    }

    @Test
    void toEntity_FromNormalPostUploadRequest() {
        // Arrange
        NormalPostUploadRequest dto = NormalPostUploadRequest.builder()
                .title("title")
                .content("content")
                .writerId(1L)
                .build();

        // Act
        NormalPost entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getTitle(), entity.getTitle());
        assertEquals(dto.getContent(), entity.getContent());
        assertEquals(dto.getWriterId(), entity.getWriter().getId());
    }

    @Test
    void toDto() {
        // Arrange
        LocalDateTime uploadedAt = LocalDateTime.of(2022, 7, 28, 22, 0, 0);
        NormalPost entity = NormalPost.builder()
                .id(1L)
                .title("title")
                .content("content")
                .writer(Member.builder()
                        .id(1L)
                        .nickname("nickname")
                        .build())
                .comments(List.of(Comment.builder().build()))
                .createdAt(uploadedAt)
                .lastModifiedAt(uploadedAt)
                .build();

        // Act
        NormalPostDto dto = mapper.toDto(entity);

        // Assert
        assertEquals(entity.getId(), dto.getPostId());
        assertEquals(entity.getTitle(), dto.getTitle());
        assertEquals(entity.getContent(), dto.getContent());
        assertEquals(entity.getWriter().getId(), dto.getWriter().getId());
        assertEquals(entity.getWriter().getNickname(), dto.getWriter().getNickname());
        assertEquals(entity.getComments().size(), dto.getCommentCount());
        assertEquals(entity.getCreatedAt(), dto.getUploadedAt());
        assertEquals(entity.getLastModifiedAt(), dto.getUploadedAt());
    }

}