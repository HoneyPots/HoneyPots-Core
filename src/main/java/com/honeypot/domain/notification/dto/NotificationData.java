package com.honeypot.domain.notification.dto;

import com.honeypot.domain.notification.entity.enums.NotificationType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class NotificationData<T extends NotificationResource> {

    private NotificationType type;

    private String titleMessage;

    private String contentMessage;

    private T resource;

}
