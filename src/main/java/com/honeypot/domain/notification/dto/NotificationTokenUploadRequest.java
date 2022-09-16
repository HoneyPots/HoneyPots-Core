package com.honeypot.domain.notification.dto;

import com.honeypot.common.validation.groups.InsertContext;
import com.honeypot.domain.notification.entity.enums.ClientType;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class NotificationTokenUploadRequest {

    @NotNull(groups = InsertContext.class)
    private Long memberId;

    @NotBlank
    private String deviceToken;

    @NotNull
    private ClientType clientType;

}
