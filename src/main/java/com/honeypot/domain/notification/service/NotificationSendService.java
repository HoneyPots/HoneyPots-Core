package com.honeypot.domain.notification.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.honeypot.domain.notification.dto.NotificationTokenDto;
import com.honeypot.domain.notification.entity.enums.NotificationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class NotificationSendService {

    private final String fcmKeyPath;

    private final String[] fcmKeyScope;

    private final NotificationTokenManageService notificationTokenManageService;

    public NotificationSendService(@Value("${fcm.key.path}") String fcmKeyPath,
                                   @Value("${fcm.key.scope}") String[] fcmKeyScope,
                                   NotificationTokenManageService notificationTokenManageService) {
        this.fcmKeyPath = fcmKeyPath;
        this.fcmKeyScope = fcmKeyScope;
        this.notificationTokenManageService = notificationTokenManageService;
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
    public void send(String token, NotificationType messageType) {
        FirebaseMessaging.getInstance().sendAsync(message(token, messageType));
    }

    @Async
    public void send(List<String> tokenList, NotificationType messageType) {
        List<Message> messages = tokenList.stream()
                .map(token -> message(token, messageType))
                .collect(Collectors.toList());
        FirebaseMessaging.getInstance().sendAllAsync(messages);
    }

    @Async
    public void send(Long memberId, NotificationType messageType) {
        List<String> tokenList
                = notificationTokenManageService.findByMemberId(memberId)
                .stream()
                .map(NotificationTokenDto::getDeviceToken)
                .toList();

        send(tokenList, messageType);
    }

    private static Message message(String token, NotificationType notificationType) {
        return Message.builder()
                .putData("time", LocalDateTime.now().toString())
                .setNotification(
                        Notification.builder()
                                .setTitle(notificationType.getTitle())
                                .setBody(notificationType.getBody())
                                .build())
                .setToken(token)
                .build();
    }

}