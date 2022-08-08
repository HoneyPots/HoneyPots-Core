package com.honeypot.domain.post.mapper;

import com.honeypot.domain.comment.entity.Comment;
import com.honeypot.domain.member.dto.WriterDto;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.post.dto.UsedTradePostDto;
import com.honeypot.domain.post.dto.UsedTradePostUploadRequest;
import com.honeypot.domain.post.entity.UsedTradePost;
import com.honeypot.domain.post.entity.enums.TradeStatus;
import com.honeypot.domain.post.entity.enums.TradeType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UsedTradePostMapperTest {

    private final UsedTradePostMapper mapper = Mappers.getMapper(UsedTradePostMapper.class);

    @Test
    void toEntity() {
        // Arrange
        LocalDateTime uploadedAt = LocalDateTime.of(2022, 7, 28, 22, 0, 0);
        UsedTradePostDto dto = UsedTradePostDto.builder()
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

                .goodsPrice(10000)
                .tradeType(TradeType.SELL)
                .tradeStatus(TradeStatus.ONGOING)
                .chatRoomLink(null)
                .build();

        // Act
        UsedTradePost entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getPostId(), entity.getId());
        assertEquals(dto.getTitle(), entity.getTitle());
        assertEquals(dto.getContent(), entity.getContent());
        assertEquals(dto.getWriter().getId(), entity.getWriter().getId());
        assertEquals(dto.getWriter().getNickname(), entity.getWriter().getNickname());
        assertEquals(dto.getGoodsPrice(), entity.getGoodsPrice());
        assertEquals(dto.getTradeType(), entity.getTradeType());
        assertEquals(dto.getTradeStatus(), entity.getTradeStatus());
        assertEquals(dto.getChatRoomLink(), entity.getChatRoomLink());
        assertEquals(dto.getUploadedAt(), entity.getCreatedAt());
        assertEquals(dto.getLastModifiedAt(), entity.getLastModifiedAt());
    }

    @Test
    void toEntity_FromUsedTradePostUploadRequest() {
        // Arrange
        UsedTradePostUploadRequest dto = UsedTradePostUploadRequest.builder()
                .title("title")
                .content("content")
                .writerId(1L)
                .attachedFiles(null)
                .goodsPrice(20000)
                .tradeType(TradeType.BUY.toString())
                .chatRoomLink("chatRoomLink")
                .build();

        // Act
        UsedTradePost entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getTitle(), entity.getTitle());
        assertEquals(dto.getContent(), entity.getContent());
        assertEquals(dto.getWriterId(), entity.getWriter().getId());
        assertEquals(dto.getGoodsPrice(), entity.getGoodsPrice());
        assertEquals(dto.getTradeType(), entity.getTradeType().toString());
        assertEquals(TradeStatus.ONGOING, entity.getTradeStatus());
    }

    @Test
    void toDto() {
        // Arrange
        LocalDateTime uploadedAt = LocalDateTime.of(2022, 7, 28, 22, 0, 0);
        UsedTradePost entity = UsedTradePost.builder()
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

                .goodsPrice(15000)
                .tradeType(TradeType.SELL)
                .tradeStatus(TradeStatus.COMPLETE)
                .chatRoomLink("https://example.com/adfsafsdca")
                .build();

        // Act
        UsedTradePostDto dto = mapper.toDto(entity);

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
        assertEquals(entity.getGoodsPrice(), dto.getGoodsPrice());
        assertEquals(entity.getTradeStatus(), dto.getTradeStatus());
        assertEquals(entity.getTradeType(), dto.getTradeType());
        assertEquals(entity.getChatRoomLink(), dto.getChatRoomLink());
    }

}