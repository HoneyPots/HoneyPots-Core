package com.honeypot.domain.auth.repository;

import com.honeypot.domain.auth.entity.AuthProvider;
import com.honeypot.domain.auth.entity.enums.AuthProviderType;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class AuthProviderRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthProviderRepository authProviderRepository;

    @Test
    @Transactional
    void findByProviderTypeAndProviderMemberId() {
        // Arrange
        String providerMemberId = "12399dfad";
        AuthProviderType providerType = AuthProviderType.KAKAO;
        createAuthProvider(providerMemberId, providerType, createMember());

        // Act
        Optional<AuthProvider> result = authProviderRepository
                .findByProviderTypeAndProviderMemberId(providerType, providerMemberId);

        // Assert
        assertTrue(result.isPresent());
    }

    @Test
    @Transactional
    void deleteByMemberId() {
        // Arrange
        String providerMemberId = "12399dfad";
        AuthProviderType providerType = AuthProviderType.KAKAO;
        Member createdMember = createMember();
        createAuthProvider(providerMemberId, providerType, createdMember);

        // Act
        int result = authProviderRepository.deleteByMemberId(createdMember.getId());

        // Assert
        assertEquals(1, result);
    }

    private Member createMember() {
        return memberRepository.save(
                Member.builder()
                        .id(1L)
                        .nickname("nickname")
                        .build());
    }

    private void createAuthProvider(String providerMemberId, AuthProviderType providerType, Member member) {
        authProviderRepository.save(
                AuthProvider.builder()
                        .providerMemberId(providerMemberId)
                        .providerType(providerType)
                        .connectDate(LocalDateTime.now())
                        .member(member)
                        .build());
    }
}