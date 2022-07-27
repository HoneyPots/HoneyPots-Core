package com.honeypot.domain.member.entity;

import com.honeypot.domain.member.enums.AuthProviderType;
import lombok.*;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;


@Entity
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthProvider {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "auth_provider_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
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

}
