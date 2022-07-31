package com.honeypot.domain.reaction.dto;

import com.honeypot.common.validation.constraints.Enum;
import com.honeypot.common.validation.groups.InsertContext;
import com.honeypot.domain.reaction.entity.enums.ReactionTarget;
import com.honeypot.domain.reaction.entity.enums.ReactionType;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
@Builder
public class ReactionRequest {

    @NotNull
    private Long targetId;

    @Enum(target = ReactionTarget.class)
    private String targetType;

    @NotNull(groups = InsertContext.class)
    private ReactionType reactionType;

    @NotNull(groups = InsertContext.class)
    private Long reactorId;

    public ReactionTarget getTargetType() {
        return ReactionTarget.valueOf(targetType.toUpperCase());
    }

}
