package com.honeypot.domain.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserEngagementKpiResponse {

    private LocalDate fromDate;

    private LocalDate toDate;

    private long totalMemberCount;

    private long totalPostCount;

    private long totalCommentCount;

    private long totalLikeReactionCount;

}
