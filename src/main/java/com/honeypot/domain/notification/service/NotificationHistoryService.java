package com.honeypot.domain.notification.service;

import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.service.MemberFindService;
import com.honeypot.domain.notification.dto.NotificationDto;
import com.honeypot.domain.notification.entity.Notification;
import com.honeypot.domain.notification.mapper.NotificationMapper;
import com.honeypot.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;

@Service
@Validated
@RequiredArgsConstructor
public class NotificationHistoryService {

    private final MemberFindService memberFindService;

    private final NotificationMapper notificationMapper;

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public Page<NotificationDto> findByMemberWithPagination(
            @NotNull Long memberId,
            @NotNull Pageable pageable
    ) {
        Member member = memberFindService.findById(memberId).orElseThrow(EntityNotFoundException::new);
        Page<Notification> page = notificationRepository.findByMemberWithPagination(member, pageable);

        return new PageImpl<>(
                page.getContent()
                        .stream()
                        .map(notificationMapper::toDto)
                        .toList(),
                page.getPageable(),
                page.getTotalElements()
        );
    }

}
