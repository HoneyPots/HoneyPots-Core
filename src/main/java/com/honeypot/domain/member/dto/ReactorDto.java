package com.honeypot.domain.member.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReactorDto {

    private Long id;

    private String nickname;

}
