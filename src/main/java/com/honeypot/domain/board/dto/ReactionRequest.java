package com.honeypot.domain.board.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Data
@Builder
public class ReactionRequest {

    @NotNull
    private Long targetId;

    // TODO Validate Enum Values
    @NotEmpty
    private String targetType;

    // TODO Validate Enum Values
    @NotEmpty
    private String reactionType;

    @NotNull
    private Long reactorId;

}
