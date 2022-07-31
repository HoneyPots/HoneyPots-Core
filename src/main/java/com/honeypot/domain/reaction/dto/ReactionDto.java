package com.honeypot.domain.reaction.dto;

import com.honeypot.domain.member.dto.ReactorDto;
import com.honeypot.domain.reaction.entity.enums.ReactionTarget;
import com.honeypot.domain.reaction.entity.enums.ReactionType;
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
