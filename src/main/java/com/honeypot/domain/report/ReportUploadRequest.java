package com.honeypot.domain.report;

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
