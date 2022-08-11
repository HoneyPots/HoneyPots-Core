package com.honeypot.domain.member.service;

import com.honeypot.domain.auth.repository.AuthProviderRepository;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class MemberWithdrawServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AuthProviderRepository authProviderRepository;

    @InjectMocks
    private MemberWithdrawService memberWithdrawService;

    @BeforeEach
    private void before() {
        this.memberWithdrawService = new MemberWithdrawService(memberRepository, authProviderRepository);
    }

    @Test
    void withdraw() {
        // Arrange
        Long memberId = 1L;
        String nickname = "nickname";
        Member createdMember = createMember(memberId, nickname);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(createdMember));
        when(memberRepository.withdrawById(memberId)).thenReturn(1);
        when(authProviderRepository.deleteByMemberId(memberId)).thenReturn(1);

        // Act
        boolean result = memberWithdrawService.withdraw(memberId);

        // Assert
        assertTrue(result);
    }

    private Member createMember(Long memberId, String nickname) {
        return Member.builder()
                .id(memberId)
                .nickname(nickname)
                .build();
    }

}