package com.honeypot.domain.notification.dto;

import com.honeypot.domain.notification.entity.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NotificationData<T extends NotificationResource> {

    private NotificationType type;

    private T resource;

}
