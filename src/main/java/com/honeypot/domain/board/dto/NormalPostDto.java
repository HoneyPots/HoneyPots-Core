package com.honeypot.domain.board.dto;

import com.honeypot.domain.member.dto.WriterDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NormalPostDto {

    private long postId;

    private String title;

    private String content;

    private WriterDto writer;

    private LocalDateTime uploadedAt;

    private LocalDateTime lastModifiedAt;

}
