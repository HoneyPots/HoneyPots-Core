package com.honeypot.domain.notification.entity;

import com.honeypot.common.entity.BaseTimeEntity;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.notification.entity.enums.ClientType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@SuperBuilder
@Getter
@Entity
@NoArgsConstructor
@Table(
        name = "notification_token",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"member_id", "device_token"}
                )
        }
)
public class NotificationToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_token_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "device_token")
    private String deviceToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "client_type")
    private ClientType clientType;

}
