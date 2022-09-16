package com.honeypot.domain.notification.repository;

import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.notification.entity.NotificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationTokenRepository extends JpaRepository<NotificationToken, Long> {

    Optional<NotificationToken> findByMemberAndDeviceToken(Member member, String deviceToken);

}
