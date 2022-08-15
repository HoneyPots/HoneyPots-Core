package com.honeypot.domain.report;

import com.honeypot.common.validation.groups.InsertContext;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ReportUploadRequest {

    @NotNull
    private ReportTarget target;

    @NotNull
    private Long targetId;

    @NotBlank
    private String reason;

    @NotNull(groups = InsertContext.class)
    private Long reporterId;

}
