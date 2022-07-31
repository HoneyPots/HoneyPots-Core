package com.honeypot.domain.board.dto;

import com.honeypot.domain.board.enums.ReactionTarget;
import com.honeypot.domain.board.enums.ReactionType;
import com.honeypot.domain.member.dto.ReactorDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReactionDto {

    private Long reactionId;

    private Long targetId;

    private ReactionTarget targetType;

    private ReactionType reactionType;

    private ReactorDto reactor;

    private boolean alreadyExists;

}
