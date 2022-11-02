package com.honeypot.domain.notification.service;

import com.honeypot.domain.notification.dto.NotificationDto;
import com.honeypot.domain.notification.dto.NotificationResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.NotNull;

public interface NotificationHistoryService {

    NotificationResource findNotificationResourceById(@NotNull Long notificationId);

    Page<NotificationDto> findByMemberWithPagination(
            @NotNull Long memberId,
            @NotNull Pageable pageable
    );

}
