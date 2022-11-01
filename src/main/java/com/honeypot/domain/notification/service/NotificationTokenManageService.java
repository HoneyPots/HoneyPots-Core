package com.honeypot.domain.notification.service;

import com.honeypot.domain.notification.dto.NotificationTokenDto;
import com.honeypot.domain.notification.dto.NotificationTokenUploadRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface NotificationTokenManageService {

    List<NotificationTokenDto> findByMemberId(@NotNull Long memberId);

    NotificationTokenDto save(@Valid NotificationTokenUploadRequest request);

    void remove(@NotNull Long memberId, @NotNull Long notificationTokenId);

}
