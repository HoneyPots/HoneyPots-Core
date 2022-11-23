package com.honeypot.domain.notification.api;

import com.honeypot.domain.notification.dto.NotificationResource;
import com.honeypot.domain.notification.service.NotificationHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationApi {

    private final NotificationHistoryService notificationHistoryService;

    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResource> getNotificationResource(
            @PathVariable Long notificationId
    ) {
        return ResponseEntity.ok(
                notificationHistoryService.findNotificationResourceById(notificationId)
        );
    }

}
