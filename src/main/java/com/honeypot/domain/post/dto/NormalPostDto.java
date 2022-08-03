package com.honeypot.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.honeypot.domain.member.dto.WriterDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
public class NormalPostDto {

    @QueryProjection
    public NormalPostDto(long postId, String title, String content, WriterDto writer,
                         Long commentCount, Long likeReactionCount, Boolean isLiked,
                         LocalDateTime uploadedAt, LocalDateTime lastModifiedAt) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.commentCount = commentCount;
        this.likeReactionCount = likeReactionCount;
        this.isLiked = isLiked;
        this.uploadedAt = uploadedAt;
        this.lastModifiedAt = lastModifiedAt;
    }

    private long postId;

    private String title;

    private String content;

    private WriterDto writer;

    @JsonInclude(NON_NULL)
    private Long commentCount;

    @JsonInclude(NON_NULL)
    private Long likeReactionCount;

    @JsonInclude(NON_NULL)
    private Boolean isLiked;

    private LocalDateTime uploadedAt;

    private LocalDateTime lastModifiedAt;

}
