package com.honeypot.domain.notification.repository;

import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.repository.MemberRepository;
import com.honeypot.domain.notification.entity.NotificationToken;
import com.honeypot.domain.notification.entity.enums.ClientType;
import com.honeypot.domain.notification.repository.NotificationTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class NotificationTokenRepositoryTest {

    @Autowired
    private NotificationTokenRepository notificationTokenRepository;

    @Autowired
    private MemberRepository memberRepository;

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