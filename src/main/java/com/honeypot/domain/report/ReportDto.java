package com.honeypot.domain.report;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReportDto {

    private Long reportId;

    private ReportTarget target;

    private Long targetId;

    private String reason;

    private Long reporterId;

    private LocalDateTime createdAt;

    private LocalDateTime lastModifiedAt;

}
