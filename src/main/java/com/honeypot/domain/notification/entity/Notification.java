package com.honeypot.domain.notification.entity;

import com.honeypot.common.entity.BaseTimeEntity;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.notification.entity.enums.NotificationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@SuperBuilder
@Getter
@Entity
@NoArgsConstructor
@Table(name = "notification")
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "message", length = 100, nullable = false)
    private String message;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private NotificationType type;

}
