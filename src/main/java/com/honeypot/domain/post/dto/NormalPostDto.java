package com.honeypot.domain.post.dto;

import com.honeypot.domain.member.dto.WriterDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NormalPostDto {

    @QueryProjection
    public NormalPostDto(long postId, String title, String content,
                         WriterDto writer, long commentCount, long likeReactionCount,
                         LocalDateTime uploadedAt, LocalDateTime lastModifiedAt) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.commentCount = commentCount;
        this.likeReactionCount = likeReactionCount;
        this.uploadedAt = uploadedAt;
        this.lastModifiedAt = lastModifiedAt;
    }

    private long postId;

    private String title;

    private String content;

    private WriterDto writer;

    private long commentCount;

    private long likeReactionCount;

    private LocalDateTime uploadedAt;

    private LocalDateTime lastModifiedAt;

}
