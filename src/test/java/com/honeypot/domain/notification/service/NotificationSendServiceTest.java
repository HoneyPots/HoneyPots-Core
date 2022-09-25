package com.honeypot.domain.notification.service;

import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.service.MemberFindService;
import com.honeypot.domain.notification.dto.NotificationTokenDto;
import com.honeypot.domain.notification.entity.Notification;
import com.honeypot.domain.notification.entity.enums.NotificationType;
import com.honeypot.domain.notification.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = NotificationSendService.class)
@ActiveProfiles(profiles = {"test", "fcm"})
class NotificationSendServiceTest {

    @MockBean
    private MemberFindService memberFindService;

    @MockBean
    private NotificationTokenManageService notificationTokenManageService;

    @MockBean
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationSendService notificationSendService;

    @Test
    @Timeout(10)
    void send_toSingleReceiver() {
        // Arrange
        String token = "f2JVeYJgpSuPx_2J4854lE:APA91bGL7lNvfZMR7_TQNXgbldxoyB11FuSoODvRggXywx4OUU7Zrg-b_1q3v5UDXwTtBi02CgHc6b9ZzF93FTHNXNpn8ewdxdhy5h8iG2gLy20y5mgj-x0yEfwx8iJ-zBDfcPQFMBae";

        // Act
        notificationSendService.send(token, NotificationType.LIKE_REACTION_TO_MY_POST);

        // Assert
        // await().atMost(10, SECONDS).until(() -> apiFuture.get() != null);
    }

    @Test
    @Timeout(10)
    void send_toMultiReceiver() {
        // Arrange
        String token = "f2JVeYJgpSuPx_2J4854lE:APA91bGL7lNvfZMR7_TQNXgbldxoyB11FuSoODvRggXywx4OUU7Zrg-b_1q3v5UDXwTtBi02CgHc6b9ZzF93FTHNXNpn8ewdxdhy5h8iG2gLy20y5mgj-x0yEfwx8iJ-zBDfcPQFMBae";
        String token2 = "eZPPh7NxWIiWMWSSRN1YLx:APA91bEnewBtElUaFzvlg_sz5f3B58o0KdGsVf3ONJH9ZnKjOhER59o__LPrGTy0qc99PuuwamX7EbT6yV7eAdiSLjiP-7YhF2XDrvYBFRBHD27V38Iqg81AGkh0d9t41Uy6QqsgJHma";
        List<String> tokens = new ArrayList<>();
        tokens.add(token);
        tokens.add(token2);

        // Act
        notificationSendService.send(tokens, NotificationType.COMMENT_TO_MY_POST);
    }

    @Test
    @Timeout(10)
    void send_toSingleMember() {
        // Arrange
        Long memberId = 1L;
        NotificationType type = NotificationType.COMMENT_TO_MY_POST;

        String token = "f2JVeYJgpSuPx_2J4854lE:APA91bGL7lNvfZMR7_TQNXgbldxoyB11FuSoODvRggXywx4OUU7Zrg-b_1q3v5UDXwTtBi02CgHc6b9ZzF93FTHNXNpn8ewdxdhy5h8iG2gLy20y5mgj-x0yEfwx8iJ-zBDfcPQFMBae";
        String token2 = "eZPPh7NxWIiWMWSSRN1YLx:APA91bEnewBtElUaFzvlg_sz5f3B58o0KdGsVf3ONJH9ZnKjOhER59o__LPrGTy0qc99PuuwamX7EbT6yV7eAdiSLjiP-7YhF2XDrvYBFRBHD27V38Iqg81AGkh0d9t41Uy6QqsgJHma";
        List<NotificationTokenDto> tokens = new ArrayList<>();
        tokens.add(NotificationTokenDto.builder().deviceToken(token).build());
        tokens.add(NotificationTokenDto.builder().deviceToken(token2).build());

        Member member = Member.builder()
                .id(memberId)
                .nickname("nickname")
                .build();

        Notification notification = Notification.builder()
                .member(member)
                .message(type.getBody())
                .type(type)
                .build();

        when(memberFindService.findById(memberId)).thenReturn(Optional.of(member));
        when(notificationTokenManageService.findByMemberId(memberId)).thenReturn(tokens);
        when(notificationRepository.save(notification)).thenReturn(any(Notification.class));

        // Act
        notificationSendService.send(memberId, type);
    }

    @Test
    void send_toSingleMember_WhenMemberNotFound() {
        // Arrange
        Long memberId = 1L;
        when(memberFindService.findById(memberId)).thenReturn(Optional.empty());

        // Act
        notificationSendService.send(memberId, NotificationType.COMMENT_TO_MY_POST);

        // Assert
        verify(notificationRepository, never()).save(any(Notification.class));
    }
}