package com.honeypot.domain.member.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WriterDto {

    @QueryProjection
    public WriterDto(long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    private long id;

    private String nickname;

}
