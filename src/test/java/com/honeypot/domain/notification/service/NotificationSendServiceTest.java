package com.honeypot.domain.notification.service;

import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.BatchResponse;
import com.honeypot.domain.notification.entity.enums.NotificationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@SpringBootTest(classes = NotificationSendService.class)
@ActiveProfiles(profiles = {"test", "fcm"})
class NotificationSendServiceTest {

    @Autowired
    private NotificationSendService notificationSendService;

    @Test
    @Timeout(10)
    void send_toSingleReceiver() {
        // Arrange
        String token = "f2JVeYJgpSuPx_2J4854lE:APA91bGL7lNvfZMR7_TQNXgbldxoyB11FuSoODvRggXywx4OUU7Zrg-b_1q3v5UDXwTtBi02CgHc6b9ZzF93FTHNXNpn8ewdxdhy5h8iG2gLy20y5mgj-x0yEfwx8iJ-zBDfcPQFMBae";

        // Act
        ApiFuture<String> apiFuture = notificationSendService.send(token, NotificationType.LIKE_REACTION_TO_MY_POST);
        await().atMost(10, SECONDS).until(() -> apiFuture.get() != null);
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
        ApiFuture<BatchResponse> apiFuture = notificationSendService.send(tokens, NotificationType.COMMENT_TO_MY_POST);
        await().atMost(10, SECONDS).until(() -> apiFuture.get().getSuccessCount() == tokens.size());
    }

}