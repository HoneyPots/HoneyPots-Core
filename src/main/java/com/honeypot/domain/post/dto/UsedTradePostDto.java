package com.honeypot.domain.post.dto;

import com.honeypot.domain.file.AttachedFileResponse;
import com.honeypot.domain.member.dto.WriterDto;
import com.honeypot.domain.post.entity.enums.TradeStatus;
import com.honeypot.domain.post.entity.enums.TradeType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
public class UsedTradePostDto extends PostDto {

    @QueryProjection
    public UsedTradePostDto(long postId, String title, String content, WriterDto writer,
                            Long commentCount, Long likeReactionCount, Boolean isLiked,
                            Long likeReactionId, AttachedFileResponse thumbnailImageFile,
                            LocalDateTime uploadedAt, LocalDateTime lastModifiedAt,
                            int goodsPrice, TradeType tradeType, TradeStatus tradeStatus,
                            String chatRoomLink) {
        super(postId, title, content, writer, commentCount, likeReactionCount, isLiked,
                likeReactionId, thumbnailImageFile, uploadedAt, lastModifiedAt);
        this.goodsPrice = goodsPrice;
        this.tradeType = tradeType;
        this.tradeStatus = tradeStatus;
        this.chatRoomLink = chatRoomLink;
    }

    @PositiveOrZero
    private int goodsPrice;

    @NotNull
    private TradeType tradeType;

    @NotNull
    private TradeStatus tradeStatus;

    private String chatRoomLink;

}
