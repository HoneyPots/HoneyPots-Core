package com.honeypot.domain.post.dto;

import com.honeypot.domain.file.AttachedFileResponse;
import com.honeypot.domain.member.dto.WriterDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false)
@Data
@SuperBuilder
@NoArgsConstructor
public class NormalPostDto extends PostDto {

    @QueryProjection
    public NormalPostDto(long postId, String title, String content, WriterDto writer,
                         Long commentCount, Long likeReactionCount, Boolean isLiked,
                         Long likeReactionId, AttachedFileResponse thumbnailImageFile,
                         LocalDateTime uploadedAt, LocalDateTime lastModifiedAt) {
        super(postId, title, content, writer, commentCount, likeReactionCount, isLiked,
                likeReactionId, thumbnailImageFile, uploadedAt, lastModifiedAt);
    }

}
