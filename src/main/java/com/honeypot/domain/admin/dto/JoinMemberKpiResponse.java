package com.honeypot.domain.admin.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
public class JoinMemberKpiResponse {

    private LocalDate date;

    private long joinMemberCount;

    @QueryProjection
    public JoinMemberKpiResponse(String date, long joinMemberCount) {
        this.date = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.joinMemberCount = joinMemberCount;
    }

}
