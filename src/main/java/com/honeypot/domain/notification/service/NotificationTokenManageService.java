package com.honeypot.domain.notification.service;

import com.honeypot.common.model.exceptions.InvalidAuthorizationException;
import com.honeypot.common.validation.groups.InsertContext;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.service.MemberFindService;
import com.honeypot.domain.notification.dto.NotificationTokenDto;
import com.honeypot.domain.notification.dto.NotificationTokenUploadRequest;
import com.honeypot.domain.notification.entity.NotificationToken;
import com.honeypot.domain.notification.entity.enums.ClientType;
import com.honeypot.domain.notification.mapper.NotificationTokenMapper;
import com.honeypot.domain.notification.repository.NotificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class NotificationTokenManageService {

    private final MemberFindService memberFindService;

    private final NotificationTokenMapper notificationTokenMapper;

    private final NotificationTokenRepository notificationTokenRepository;

    @Transactional
    @Validated(InsertContext.class)
    public NotificationTokenDto saveNotificationToken(@Valid NotificationTokenUploadRequest request) {
        Member member = memberFindService.findById(request.getMemberId()).orElseThrow(EntityNotFoundException::new);
        String deviceToken = request.getDeviceToken();
        ClientType clientType = request.getClientType();

        Optional<NotificationToken> tokenOptional
                = notificationTokenRepository.findByMemberAndDeviceToken(member, deviceToken);

        NotificationToken createdOrUpdated;
        if (tokenOptional.isEmpty()) {
            // Save if there are no existing rows
            createdOrUpdated = notificationTokenRepository.save(NotificationToken.builder()
                    .deviceToken(deviceToken)
                    .clientType(clientType)
                    .member(member)
                    .build());
        } else {
            // Update lastModifiedAt column when re-upload request occurred.
            NotificationToken exists = tokenOptional.get();
            createdOrUpdated = notificationTokenRepository.save(NotificationToken.builder()
                    .id(exists.getId())
                    .member(member)
                    .deviceToken(exists.getDeviceToken())
                    .clientType(exists.getClientType())
                    .createdAt(exists.getCreatedAt())
                    .lastModifiedAt(LocalDateTime.now())
                    .build());
        }

        return notificationTokenMapper.toDto(createdOrUpdated);
    }

    @Transactional
    @Validated
    public void removeNotificationToken(@NotNull Long memberId, @NotNull Long notificationTokenId) {
        memberFindService.findById(memberId).orElseThrow(EntityNotFoundException::new);
        NotificationToken token = notificationTokenRepository
                .findById(notificationTokenId)
                .orElseThrow(EntityNotFoundException::new);

        if (!memberId.equals(token.getMember().getId())) {
            throw new InvalidAuthorizationException();
        }

        notificationTokenRepository.delete(token);
    }

}
