package com.honeypot.domain.post.dto;

import com.honeypot.domain.member.dto.WriterDto;
import com.honeypot.domain.post.entity.Post;
import com.honeypot.domain.post.entity.enums.PostType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@EqualsAndHashCode
public class SimplePostDto {

    private Long postId;

    private PostType postType;

    private String title;

    private String content;

    private WriterDto writer;

    private LocalDateTime uploadedAt;

    private LocalDateTime lastModifiedAt;

    public static SimplePostDto toDto(Post post) {
        return SimplePostDto.builder()
                .postId(post.getId())
                .postType(post.getType())
                .title(post.getTitle())
                .content(post.getContent())
                .writer(WriterDto.builder()
                        .id(post.getWriter().getId())
                        .nickname(post.getWriter().getNickname())
                        .build()
                )
                .uploadedAt(post.getCreatedAt())
                .lastModifiedAt(post.getLastModifiedAt())
                .build();
    }

}
