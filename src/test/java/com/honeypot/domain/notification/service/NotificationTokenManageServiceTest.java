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
import java.util.ArrayList;
import java.util.List;
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
    private NotificationTokenManageServiceImpl notificationTokenManageService;

    @BeforeEach
    private void before() {
        this.notificationTokenManageService = new NotificationTokenManageServiceImpl(
                memberFindService,
                notificationTokenMapperMock,
                notificationTokenRepository
        );
    }

    @Test
    void findByMemberId() {
        // Arrange
        Long memberId = 1212414L;
        Member member = Member.builder().id(1L).nickname("nickname").build();

        List<NotificationToken> tokens = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tokens.add(create(i + 1L, "token" + i, ClientType.WEB, member));
        }

        when(memberFindService.findById(memberId)).thenReturn(Optional.of(member));
        when(notificationTokenRepository.findByMember(member)).thenReturn(tokens);

        // Act
        List<NotificationTokenDto> result = notificationTokenManageService.findByMemberId(memberId);

        // Assert
        assertNotNull(result);
        assertEquals(tokens.size(), result.size());
    }

    @Test
    void save_UploadNewToken() {
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
        NotificationTokenDto result = notificationTokenManageService.save(request);

        // Assert
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void save_UpdateExistsToken() {
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
        NotificationTokenDto result = notificationTokenManageService.save(request);

        // Assert
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void save_MemberNotFound() {
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
            notificationTokenManageService.save(request);
        });
    }

    @Test
    void remove() {
        // Arrange
        Member member = Member.builder().id(1L).build();
        Long notificationTokenId = 192L;

        NotificationToken exists = create(notificationTokenId, "token", ClientType.WEB, member);

        when(memberFindService.findById(member.getId())).thenReturn(Optional.of(member));
        when(notificationTokenRepository.findById(notificationTokenId)).thenReturn(Optional.of(exists));
        doNothing().when(notificationTokenRepository).delete(exists);

        // Act
        notificationTokenManageService.remove(member.getId(), notificationTokenId);

        // Assert
        verify(notificationTokenRepository, times(1)).delete(exists);
    }

    @Test
    void remove_MemberNotFound() {
        // Arrange
        Member member = Member.builder().id(1L).build();
        Long notificationTokenId = 192L;

        when(memberFindService.findById(member.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            notificationTokenManageService.remove(member.getId(), notificationTokenId);
        });
    }

    @Test
    void remove_InvalidAuthorizationException() {
        // Arrange
        Member member = Member.builder().id(1L).build();
        Long notificationTokenId = 192L;

        NotificationToken exists = create(notificationTokenId, "token", ClientType.WEB,
                Member.builder().id(member.getId() + 1).build());

        when(memberFindService.findById(member.getId())).thenReturn(Optional.of(member));
        when(notificationTokenRepository.findById(notificationTokenId)).thenReturn(Optional.of(exists));

        // Act & Assert
        assertThrows(InvalidAuthorizationException.class, () -> {
            notificationTokenManageService.remove(member.getId(), notificationTokenId);
        });
    }

    private NotificationToken create(Long id, String token, ClientType clientType, Member member) {
        LocalDateTime now = LocalDateTime.now();
        return NotificationToken.builder()
                .id(id)
                .deviceToken(token)
                .clientType(clientType)
                .member(member)
                .createdAt(now)
                .lastModifiedAt(now)
                .build();
    }
}