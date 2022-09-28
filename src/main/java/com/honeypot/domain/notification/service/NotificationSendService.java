package com.honeypot.domain.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.service.MemberFindService;
import com.honeypot.domain.notification.dto.NotificationData;
import com.honeypot.domain.notification.dto.NotificationResource;
import com.honeypot.domain.notification.dto.NotificationTokenDto;
import com.honeypot.domain.notification.entity.enums.NotificationType;
import com.honeypot.domain.notification.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class NotificationSendService {

    private static final String MESSAGE_TITLE = "꿀단지";

    private static final String MESSAGE_COMMENT_TO_POST = "'%s'님이 새로운 댓글을 남겼습니다.";

    private static final String MESSAGE_LIKE_REACTION_TO_POST = "'%s'님이 게시글을 좋아합니다.";

    private final String fcmKeyPath;

    private final String[] fcmKeyScope;

    private final MemberFindService memberFindService;

    private final NotificationTokenManageService notificationTokenManageService;

    private final NotificationRepository notificationRepository;

    private final ObjectMapper objectMapper;

    public NotificationSendService(@Value("${fcm.key.path}") String fcmKeyPath,
                                   @Value("${fcm.key.scope}") String[] fcmKeyScope,
                                   MemberFindService memberFindService,
                                   NotificationTokenManageService notificationTokenManageService,
                                   NotificationRepository notificationRepository,
                                   ObjectMapper objectMapper) {
        this.fcmKeyPath = fcmKeyPath;
        this.fcmKeyScope = fcmKeyScope;
        this.memberFindService = memberFindService;
        this.notificationTokenManageService = notificationTokenManageService;
        this.notificationRepository = notificationRepository;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(
                                GoogleCredentials
                                        .fromStream(new ClassPathResource(fcmKeyPath).getInputStream())
                                        .createScoped(List.of(fcmKeyScope))
                        )
                        .build();

                FirebaseApp.initializeApp(options);
                log.info("Firebase application has been initialized");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    private <T extends NotificationResource> void send(List<String> tokenList, NotificationData<T> data) {
        List<Message> messages = tokenList.stream()
                .map(token -> message(token, data))
                .collect(Collectors.toList());
        FirebaseMessaging.getInstance().sendAllAsync(messages);
    }

    @Async
    public <T extends NotificationResource> void send(Long memberId, NotificationData<T> data) {
        Optional<Member> member = memberFindService.findById(memberId);
        if (member.isEmpty()) {
            return;
        }

        String message = data.getType() == NotificationType.COMMENT_TO_POST ?
                MESSAGE_COMMENT_TO_POST : MESSAGE_LIKE_REACTION_TO_POST;

        notificationRepository.save(
                com.honeypot.domain.notification.entity.Notification.builder()
                        .member(member.get())
                        .titleMessage(message)
                        .contentMessage(message)
                        .type(data.getType())
                        .build()
        );

        List<String> tokenList
                = notificationTokenManageService.findByMemberId(memberId)
                .stream()
                .map(NotificationTokenDto::getDeviceToken)
                .toList();

        send(tokenList, data);
    }

    private <T extends NotificationResource> Message message(String token, NotificationData<T> data) {
        String dataJson = "{}";

        // Send notification finally when json processing exception occurred
        try {
            dataJson = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

        String message = data.getType() == NotificationType.COMMENT_TO_POST ?
                MESSAGE_COMMENT_TO_POST : MESSAGE_LIKE_REACTION_TO_POST;

        return Message.builder()
                .putData("time", LocalDateTime.now().toString())
                .putData("data", dataJson)
                .setNotification(
                        Notification.builder()
                                .setTitle(MESSAGE_TITLE)
                                .setBody(message)
                                .build())
                .setToken(token)
                .build();
    }

}