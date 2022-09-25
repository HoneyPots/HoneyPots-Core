package com.honeypot.domain.notification.repository;

import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByMember(Member member, Pageable pageable);

}
