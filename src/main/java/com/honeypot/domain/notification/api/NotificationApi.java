package com.honeypot.domain.notification.api;

import com.honeypot.common.model.exceptions.InvalidTokenException;
import com.honeypot.common.utils.SecurityUtils;
import com.honeypot.domain.notification.dto.NotificationTokenDto;
import com.honeypot.domain.notification.service.NotificationTokenManageService;
import com.honeypot.domain.notification.dto.NotificationTokenUploadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationApi {

    private final NotificationTokenManageService notificationTokenManageService;

    @PostMapping("/tokens")
    public ResponseEntity<?> saveNotificationToken(@Valid @RequestBody NotificationTokenUploadRequest request) {
        Long memberId = SecurityUtils.getCurrentMemberId().orElseThrow(InvalidTokenException::new);
        request.setMemberId(memberId);

        NotificationTokenDto created = notificationTokenManageService.saveNotificationToken(request);

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
        notificationTokenManageService.removeNotificationToken(memberId, tokenId);

        return ResponseEntity.noContent().build();
    }

}
