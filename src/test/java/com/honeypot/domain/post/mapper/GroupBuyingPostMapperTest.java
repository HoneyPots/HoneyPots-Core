package com.honeypot.domain.post.mapper;

import com.honeypot.domain.comment.entity.Comment;
import com.honeypot.domain.member.dto.WriterDto;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.post.dto.GroupBuyingPostDto;
import com.honeypot.domain.post.dto.GroupBuyingPostUploadRequest;
import com.honeypot.domain.post.entity.GroupBuyingPost;
import com.honeypot.domain.post.entity.enums.GroupBuyingStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GroupBuyingPostMapperTest {

    private final GroupBuyingPostMapper mapper = Mappers.getMapper(GroupBuyingPostMapper.class);

    @Test
    void toEntity() {
        // Arrange
        LocalDateTime uploadedAt = LocalDateTime.of(2022, 7, 28, 22, 0, 0);
        GroupBuyingPostDto dto = GroupBuyingPostDto.builder()
                .postId(1)
                .title("title")
                .content("content")
                .writer(WriterDto.builder()
                        .id(1)
                        .nickname("nickname")
                        .build())
                .commentCount(10L)
                .likeReactionCount(5L)
                .isLiked(true)
                .likeReactionId(10L)
                .thumbnailImageFile(null)
                .uploadedAt(uploadedAt)
                .lastModifiedAt(uploadedAt)

                .category("한식")
                .groupBuyingStatus(GroupBuyingStatus.COMPLETE)
                .chatRoomLink(null)
                .deadline(LocalDateTime.now().plus(20000L, ChronoUnit.SECONDS))
                .build();

        // Act
        GroupBuyingPost entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getPostId(), entity.getId());
        assertEquals(dto.getTitle(), entity.getTitle());
        assertEquals(dto.getContent(), entity.getContent());
        assertEquals(dto.getWriter().getId(), entity.getWriter().getId());
        assertEquals(dto.getWriter().getNickname(), entity.getWriter().getNickname());
        assertEquals(dto.getCategory(), entity.getCategory());
        assertEquals(dto.getGroupBuyingStatus(), entity.getGroupBuyingStatus());
        assertEquals(dto.getChatRoomLink(), entity.getChatRoomLink());
        assertEquals(dto.getDeadline(), entity.getDeadline());
        assertEquals(dto.getUploadedAt(), entity.getCreatedAt());
        assertEquals(dto.getLastModifiedAt(), entity.getLastModifiedAt());
    }

    @Test
    void toEntity_FromUsedTradePostUploadRequest() {
        // Arrange
        GroupBuyingPostUploadRequest dto = GroupBuyingPostUploadRequest.builder()
                .title("title")
                .content("content")
                .writerId(1L)
                .attachedFiles(null)
                .category("한식")
                .groupBuyingStatus("ONGOING")
                .chatRoomLink("chatRoomLink")
                .deadline(LocalDateTime.now().plus(20000L, ChronoUnit.SECONDS))
                .build();

        // Act
        GroupBuyingPost entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getTitle(), entity.getTitle());
        assertEquals(dto.getContent(), entity.getContent());
        assertEquals(dto.getWriterId(), entity.getWriter().getId());
        assertEquals(dto.getCategory(), entity.getCategory());
        assertEquals(dto.getGroupBuyingStatus(), entity.getGroupBuyingStatus().toString());
    }

    @Test
    void toDto() {
        // Arrange
        LocalDateTime uploadedAt = LocalDateTime.of(2022, 7, 28, 22, 0, 0);
        GroupBuyingPost entity = GroupBuyingPost.builder()
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

                .category("중식")
                .groupBuyingStatus(GroupBuyingStatus.COMPLETE)
                .chatRoomLink("https://example.com/adfsafsdca")
                .deadline(LocalDateTime.now())
                .build();

        // Act
        GroupBuyingPostDto dto = mapper.toDto(entity);

        // Assert
        assertEquals(entity.getId(), dto.getPostId());
        assertEquals(entity.getTitle(), dto.getTitle());
        assertEquals(entity.getContent(), dto.getContent());
        assertEquals(entity.getWriter().getId(), dto.getWriter().getId());
        assertEquals(entity.getWriter().getNickname(), dto.getWriter().getNickname());
        assertNull(dto.getCommentCount());
        assertNull(dto.getLikeReactionCount());
        assertNull(dto.getLikeReactionId());
        assertNull(dto.getIsLiked());
        assertEquals(entity.getCreatedAt(), dto.getUploadedAt());
        assertEquals(entity.getLastModifiedAt(), dto.getUploadedAt());
        assertEquals(entity.getCategory(), dto.getCategory());
        assertEquals(entity.getGroupBuyingStatus(), dto.getGroupBuyingStatus());
        assertEquals(entity.getChatRoomLink(), dto.getChatRoomLink());
        assertEquals(entity.getDeadline(), dto.getDeadline());
    }

}