package com.honeypot.domain.post.dto;

import com.honeypot.domain.file.AttachedFileResponse;
import com.honeypot.domain.member.dto.WriterDto;
import com.honeypot.domain.post.entity.enums.GroupBuyingStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class GroupBuyingPostDto extends NormalPostDto {

    @QueryProjection
    public GroupBuyingPostDto(long postId, String title, String content, WriterDto writer,
                              Long commentCount, Long likeReactionCount, Boolean isLiked,
                              Long likeReactionId, AttachedFileResponse thumbnailImageFile,
                              LocalDateTime uploadedAt, LocalDateTime lastModifiedAt,
                              int goodsPrice, String category, GroupBuyingStatus groupBuyingStatus,
                              String chatRoomLink, LocalDateTime deadline) {
        super(postId, title, content, writer, commentCount, likeReactionCount, isLiked,
                likeReactionId, thumbnailImageFile, uploadedAt, lastModifiedAt);
        this.goodsPrice = goodsPrice;
        this.category = category;
        this.groupBuyingStatus = groupBuyingStatus;
        this.chatRoomLink = chatRoomLink;
        this.deadline = deadline;
    }

    @PositiveOrZero
    private int goodsPrice;

    @NotNull
    private String category;

    @NotNull
    private GroupBuyingStatus groupBuyingStatus;

    private String chatRoomLink;

    @NotNull
    private LocalDateTime deadline;

}
