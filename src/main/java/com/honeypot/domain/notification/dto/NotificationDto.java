package com.honeypot.domain.notification.dto;

import com.honeypot.domain.notification.entity.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class NotificationDto {

    private Long notificationId;

    private String titleMessage;

    private String contentMessage;

    private NotificationType type;

    private LocalDateTime createdAt;

    private LocalDateTime lastModifiedAt;

}
