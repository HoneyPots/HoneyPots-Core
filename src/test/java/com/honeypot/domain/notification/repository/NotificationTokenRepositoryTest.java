package com.honeypot.domain.notification.repository;

import com.honeypot.config.TestConfig;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.repository.MemberRepository;
import com.honeypot.domain.notification.entity.NotificationToken;
import com.honeypot.domain.notification.entity.enums.ClientType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({TestConfig.class})
class NotificationTokenRepositoryTest {

    @Autowired
    private NotificationTokenRepository notificationTokenRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void findByMember() {
        // Arrange
        String deviceToken = "notificationDeviceToken";

        Member member = createMember("testNickname");

        int tokenCount = 5;
        for (int i = 0; i < tokenCount; i++) {
            createNotificationToken(member, deviceToken + i, ClientType.WEB);
        }

        // Act
        List<NotificationToken> result = notificationTokenRepository.findByMember(member);

        // Assert
        assertNotNull(result);
        assertEquals(tokenCount, result.size());
    }

    @Test
    void findByMemberAndDeviceToken() {
        // Arrange
        String deviceToken = "notificationDeviceToken";

        Member member = createMember("testNickname");
        createNotificationToken(member, deviceToken, ClientType.WEB);

        // Act
        Optional<NotificationToken> result
                = notificationTokenRepository.findByMemberAndDeviceToken(member, deviceToken);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(member, result.get().getMember());
        assertEquals(deviceToken, result.get().getDeviceToken());
    }

    private Member createMember(String nickname) {
        Member member = Member.builder()
                .nickname(nickname)
                .build();
        return memberRepository.save(member);
    }

    private NotificationToken createNotificationToken(Member member, String token, ClientType clientType) {
        NotificationToken created = NotificationToken.builder()
                .member(member)
                .deviceToken(token)
                .clientType(clientType)
                .build();
        return notificationTokenRepository.save(created);
    }

}