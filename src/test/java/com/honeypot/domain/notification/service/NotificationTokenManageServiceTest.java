package com.honeypot.domain.notification.service;

import com.honeypot.common.model.exceptions.InvalidAuthorizationException;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.service.MemberFindService;
import com.honeypot.domain.notification.dto.NotificationTokenDto;
import com.honeypot.domain.notification.dto.NotificationTokenUploadRequest;
import com.honeypot.domain.notification.entity.NotificationToken;
import com.honeypot.domain.notification.entity.enums.ClientType;
import com.honeypot.domain.notification.mapper.NotificationTokenMapper;
import com.honeypot.domain.notification.repository.NotificationTokenRepository;
import com.honeypot.domain.notification.service.NotificationTokenManageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class NotificationTokenManageServiceTest {

    private final NotificationTokenMapper notificationTokenMapper = Mappers.getMapper(NotificationTokenMapper.class);

    @Mock
    private MemberFindService memberFindService;

    @Mock
    private NotificationTokenRepository notificationTokenRepository;

    @Mock
    private NotificationTokenMapper notificationTokenMapperMock;

    @InjectMocks
    private NotificationTokenManageService notificationTokenManageService;

    @BeforeEach
    private void before() {
        this.notificationTokenManageService = new NotificationTokenManageService(
                memberFindService,
                notificationTokenMapperMock,
                notificationTokenRepository
        );
    }

    @Test
    void saveNotificationToken_UploadNewToken() {
        // Arrange
        Member member = Member.builder().id(1L).nickname("nickname").build();
        NotificationTokenUploadRequest request = NotificationTokenUploadRequest.builder()
                .memberId(member.getId())
                .deviceToken("notificationDeviceToken")
                .clientType(ClientType.WEB)
                .build();

        when(memberFindService.findById(member.getId())).thenReturn(Optional.of(member));
        when(notificationTokenRepository.findByMemberAndDeviceToken(member, request.getDeviceToken()))
                .thenReturn(Optional.empty());

        NotificationToken created = NotificationToken.builder()
                .deviceToken(request.getDeviceToken())
                .clientType(request.getClientType())
                .member(member)
                .build();

        when(notificationTokenRepository.save(any(NotificationToken.class)))
                .thenAnswer((invocation) -> {
                    LocalDateTime createdAt = LocalDateTime.now();
                    NotificationToken arg = invocation.getArgument(0);
                    return NotificationToken.builder()
                            .id(1L)
                            .member(arg.getMember())
                            .deviceToken(arg.getDeviceToken())
                            .clientType(arg.getClientType())
                            .createdAt(createdAt)
                            .lastModifiedAt(createdAt)
                            .build();
                });

        NotificationTokenDto expected = notificationTokenMapper.toDto(created);
        when(notificationTokenMapperMock.toDto(any(NotificationToken.class))).thenReturn(expected);

        // Act
        NotificationTokenDto result = notificationTokenManageService.saveNotificationToken(request);

        // Assert
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void saveNotificationToken_UpdateExistsToken() {
        // Arrange
        Member member = Member.builder().id(1L).nickname("nickname").build();
        NotificationTokenUploadRequest request = NotificationTokenUploadRequest.builder()
                .memberId(member.getId())
                .deviceToken("notificationDeviceToken")
                .clientType(ClientType.WEB)
                .build();

        LocalDateTime createdAt = LocalDateTime.now();
        NotificationToken exists = NotificationToken.builder()
                .id(1L)
                .deviceToken(request.getDeviceToken())
                .clientType(request.getClientType())
                .member(member)
                .createdAt(createdAt)
                .lastModifiedAt(createdAt)
                .build();

        when(memberFindService.findById(member.getId())).thenReturn(Optional.of(member));
        when(notificationTokenRepository.findByMemberAndDeviceToken(member, request.getDeviceToken()))
                .thenReturn(Optional.of(exists));

        when(notificationTokenRepository.save(any(NotificationToken.class)))
                .thenAnswer((invocation) -> {
                    LocalDateTime modifiedAt = LocalDateTime.now();
                    NotificationToken arg = invocation.getArgument(0);
                    return NotificationToken.builder()
                            .id(1L)
                            .member(arg.getMember())
                            .deviceToken(arg.getDeviceToken())
                            .clientType(arg.getClientType())
                            .createdAt(createdAt)
                            .lastModifiedAt(modifiedAt)
                            .build();
                });
        NotificationTokenDto expected = notificationTokenMapper.toDto(exists);
        when(notificationTokenMapperMock.toDto(any(NotificationToken.class))).thenReturn(expected);

        // Act
        NotificationTokenDto result = notificationTokenManageService.saveNotificationToken(request);

        // Assert
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void saveNotificationToken_MemberNotFound() {
        // Arrange
        Member member = Member.builder().id(1L).nickname("nickname").build();
        NotificationTokenUploadRequest request = NotificationTokenUploadRequest.builder()
                .memberId(member.getId())
                .deviceToken("notificationDeviceToken")
                .clientType(ClientType.IOS)
                .build();

        when(memberFindService.findById(member.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            notificationTokenManageService.saveNotificationToken(request);
        });
    }

}