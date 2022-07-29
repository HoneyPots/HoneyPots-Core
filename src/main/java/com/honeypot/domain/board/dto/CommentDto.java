package com.honeypot.domain.board.dto;

import com.honeypot.domain.member.dto.WriterDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {

    private long postId;

    private long commentId;

    private String content;

    private WriterDto writer;

    private LocalDateTime createdAt;

    private LocalDateTime lastModifiedAt;

}
