package com.honeypot.domain.notification.api;

import com.honeypot.common.model.exceptions.InvalidTokenException;
import com.honeypot.common.utils.SecurityUtils;
import com.honeypot.common.validation.constraints.AllowedSortProperties;
import com.honeypot.domain.notification.dto.NotificationResource;
import com.honeypot.domain.notification.dto.NotificationTokenDto;
import com.honeypot.domain.notification.repository.NotificationRepository;
import com.honeypot.domain.notification.service.NotificationHistoryService;
import com.honeypot.domain.notification.service.NotificationTokenManageService;
import com.honeypot.domain.notification.dto.NotificationTokenUploadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationApi {

    private final NotificationTokenManageService notificationTokenManageService;

    private final NotificationHistoryService notificationHistoryService;

    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResource> getNotificationResource(
            @PathVariable Long notificationId
    ) {
        return ResponseEntity.ok(
                notificationHistoryService.findNotificationResourceById(notificationId)
        );
    }

    @PostMapping("/tokens")
    public ResponseEntity<?> saveNotificationToken(@Valid @RequestBody NotificationTokenUploadRequest request) {
        Long memberId = SecurityUtils.getCurrentMemberId().orElseThrow(InvalidTokenException::new);
        request.setMemberId(memberId);

        NotificationTokenDto created = notificationTokenManageService.save(request);

        return ResponseEntity
                .created(ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{tokenId}")
                        .buildAndExpand(created.getNotificationTokenId())
                        .toUri())
                .body(created);
    }

    @DeleteMapping("/tokens/{tokenId}")
    public ResponseEntity<?> removeNotificationToken(@PathVariable Long tokenId) {
        Long memberId = SecurityUtils.getCurrentMemberId().orElseThrow(InvalidTokenException::new);
        notificationTokenManageService.remove(memberId, tokenId);

        return ResponseEntity.noContent().build();
    }

}
