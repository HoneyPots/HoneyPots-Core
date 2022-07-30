package com.honeypot.domain.board.mapper;

import com.honeypot.domain.board.dto.CommentDto;
import com.honeypot.domain.board.dto.CommentUploadRequest;
import com.honeypot.domain.board.entity.Comment;
import com.honeypot.domain.board.entity.NormalPost;
import com.honeypot.domain.member.dto.WriterDto;
import com.honeypot.domain.member.entity.Member;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {

    private final CommentMapper mapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void toEntity() {
        // Arrange
        LocalDateTime createdAt = LocalDateTime.of(2022, 7, 28, 22, 0, 0);
        CommentDto dto = CommentDto.builder()
                .postId(1)
                .content("content")
                .writer(WriterDto.builder()
                        .id(1)
                        .nickname("nickname")
                        .build())
                .createdAt(createdAt)
                .lastModifiedAt(createdAt)
                .build();

        // Act
        Comment entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getCommentId(), entity.getId());
        assertEquals(dto.getPostId(), entity.getPost().getId());
        assertEquals(dto.getContent(), entity.getContent());
        assertEquals(dto.getWriter().getId(), entity.getWriter().getId());
        assertEquals(dto.getWriter().getNickname(), entity.getWriter().getNickname());
        assertEquals(dto.getCreatedAt(), entity.getCreatedAt());
        assertEquals(dto.getLastModifiedAt(), entity.getLastModifiedAt());
    }

    @Test
    void toEntity_FromCommentUploadRequest() {
        // Arrange
        CommentUploadRequest dto = CommentUploadRequest.builder()
                .postId(1L)
                .content("content")
                .writerId(1L)
                .build();

        // Act
        Comment entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getPostId(), entity.getPost().getId());
        assertEquals(dto.getContent(), entity.getContent());
        assertEquals(dto.getWriterId(), entity.getWriter().getId());
    }

    @Test
    void toDto() {
        // Arrange
        LocalDateTime uploadedAt = LocalDateTime.of(2022, 7, 28, 22, 0, 0);
        Comment entity = Comment.builder()
                .id(1L)
                .post(NormalPost.builder()
                        .id(1L)
                        .title("post title")
                        .content("post content")
                        .writer(Member.builder()
                                .id(1L)
                                .nickname("nickname")
                                .build())
                        .build())
                .content("content")
                .writer(Member.builder()
                        .id(1L)
                        .nickname("nickname")
                        .build())
                .createdAt(uploadedAt)
                .lastModifiedAt(uploadedAt)
                .build();

        // Act
        CommentDto dto = mapper.toDto(entity);

        // Assert
        assertEquals(entity.getId(), dto.getCommentId());
        assertEquals(entity.getPost().getId(), dto.getPostId());
        assertEquals(entity.getContent(), dto.getContent());
        assertEquals(entity.getWriter().getId(), dto.getWriter().getId());
        assertEquals(entity.getWriter().getNickname(), dto.getWriter().getNickname());
        assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
        assertEquals(entity.getLastModifiedAt(), dto.getLastModifiedAt());
    }

}