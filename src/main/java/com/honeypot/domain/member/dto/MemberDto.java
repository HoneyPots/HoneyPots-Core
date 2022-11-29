package com.honeypot.domain.member.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class MemberDto {

    @QueryProjection
    public MemberDto(long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    private Long id;

    private String nickname;

    private LocalDateTime createdAt;

    private LocalDateTime lastModifiedAt;

}
