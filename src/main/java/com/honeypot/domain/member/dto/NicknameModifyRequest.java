package com.honeypot.domain.member.dto;

import com.honeypot.common.validation.groups.InsertContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NicknameModifyRequest {

    @NotBlank
    private String nickname;

    @NotNull(groups = InsertContext.class)
    private Long memberId;

}
