package com.honeypot.domain.board.dto;

import com.honeypot.domain.member.dto.ReactorDto;
import com.honeypot.domain.member.dto.WriterDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReactionDto {

    private Long reactionId;

    private Long targetId;

    private String targetType;

    private String reactionType;

    private ReactorDto reactor;

}
