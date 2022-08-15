package com.honeypot.common.model.exceptions;

import com.honeypot.domain.report.ReportTarget;

public class ReportTargetNotFoundException extends BaseException {

    public ReportTargetNotFoundException(ReportTarget target, Long targetId) {
        super("HRE002", String.format("ReportTarget [%s:%d] not found.", target, targetId));
    }

}
