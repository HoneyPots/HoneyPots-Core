package com.honeypot.domain.auth.entity;

import com.honeypot.common.entity.BaseTimeEntity;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.auth.entity.enums.AuthProviderType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;


@Entity
@Getter
@ToString
@SuperBuilder
@NoArgsConstructor
public class AuthProvider extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "auth_provider_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @ToString.Exclude
    private Member member;

    @Column(name = "provider_member_id", nullable = false)
    private Long providerMemberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", nullable = false)
    private AuthProviderType providerType;

    @Column(name = "access_token", nullable = false)
    private String accessToken;

    @Column(name = "connect_date", nullable = false)
    private LocalDateTime connectDate;

    public void setMember(Member member) {
        this.member = member;
    }

}
