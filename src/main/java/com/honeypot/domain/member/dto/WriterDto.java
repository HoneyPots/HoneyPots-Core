package com.honeypot.domain.member.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WriterDto {

    private long id;

    private String nickname;

}
