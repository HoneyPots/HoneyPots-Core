package com.honeypot.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.honeypot.domain.file.AttachedFileResponse;
import com.honeypot.domain.member.dto.WriterDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class PostDto {

    @QueryProjection
    public PostDto(Long postId, String title, String content, WriterDto writer,
                   Long commentCount, Long likeReactionCount, Boolean isLiked,
                   Long likeReactionId, AttachedFileResponse thumbnailImageFile,
                   LocalDateTime uploadedAt, LocalDateTime lastModifiedAt) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.commentCount = commentCount;
        this.likeReactionCount = likeReactionCount;
        this.isLiked = isLiked;
        this.likeReactionId = likeReactionId;
        this.thumbnailImageFile = thumbnailImageFile;
        this.uploadedAt = uploadedAt;
        this.lastModifiedAt = lastModifiedAt;
    }

    private Long postId;

    private String title;

    private String content;

    private WriterDto writer;

    @JsonInclude(NON_NULL)
    private Long commentCount;

    @JsonInclude(NON_NULL)
    private Long likeReactionCount;

    @JsonInclude(NON_NULL)
    private Boolean isLiked;

    @JsonInclude(NON_NULL)
    private Long likeReactionId;

    @JsonInclude(NON_NULL)
    private AttachedFileResponse thumbnailImageFile;

    @JsonInclude(NON_NULL)
    private List<AttachedFileResponse> attachedFiles;

    private LocalDateTime uploadedAt;

    private LocalDateTime lastModifiedAt;

}
