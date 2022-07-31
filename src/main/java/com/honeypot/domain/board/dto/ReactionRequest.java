package com.honeypot.domain.board.dto;

import com.honeypot.common.validation.constraints.Enum;
import com.honeypot.common.validation.groups.InsertContext;
import com.honeypot.domain.board.enums.ReactionTarget;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Data
@Builder
public class ReactionRequest {

    @NotNull
    private Long targetId;

    @Enum(target = ReactionTarget.class)
    private String targetType;

    // TODO Validate Enum Values
    @NotEmpty
    private String reactionType;

    @NotNull(groups = InsertContext.class)
    private Long reactorId;

    public ReactionTarget getTargetType() {
        return ReactionTarget.valueOf(targetType.toUpperCase());
    }
}
