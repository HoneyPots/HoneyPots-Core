package com.honeypot.domain.member.service;

import com.honeypot.domain.auth.entity.AuthProvider;
import com.honeypot.domain.auth.entity.enums.AuthProviderType;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.entity.enums.Gender;
import com.honeypot.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class MemberSignupServiceTest {

    @Mock
    private MemberRepository memberRepository;

    private MemberSignupService memberSignupService;

    @BeforeEach
    private void before() {
        this.memberSignupService = new MemberSignupService(memberRepository);
    }

    @Test
    void signup() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = new Timestamp(1237129848L);
        Member created = Member.builder()
                .id(1L)
                .nickname("헤리움" + timestamp.getTime())
                .ageRange("20-30")
                .birthday("0404")
                .email("example@gmail.com")
                .gender(Gender.MALE)
                .isWithdrawal(false)
                .createdAt(now)
                .lastModifiedAt(now)
                .build();

        when(memberRepository.save(any(Member.class))).thenReturn(created);

        // Act
        Member result = memberSignupService.signup();

        // Assert
        assertNotNull(result);
        assertEquals(created, result);
    }

    @Test
    void signupWithOAuth() {
        // Arrange
        String providerMemberId = "1238123321";
        AuthProviderType providerType = AuthProviderType.KAKAO;
        LocalDateTime oAuthConnectDate = LocalDateTime.now();

        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = new Timestamp(1237129848L);
        Member created = Member.builder()
                .id(1L)
                .nickname("헤리움" + timestamp.getTime())
                .ageRange("20-30")
                .birthday("0404")
                .email("example@gmail.com")
                .gender(Gender.MALE)
                .isWithdrawal(false)
                .createdAt(now)
                .lastModifiedAt(now)
                .authProvider(AuthProvider.builder()
                        .providerMemberId(providerMemberId)
                        .providerType(providerType)
                        .connectDate(oAuthConnectDate)
                        .createdAt(now)
                        .lastModifiedAt(now)
                        .build())
                .build();

        when(memberRepository.save(any(Member.class))).thenReturn(created);

        // Act
        Member result = memberSignupService.signupWithOAuth(providerMemberId, providerType, oAuthConnectDate);

        // Assert
        assertNotNull(result);
        assertEquals(created, result);
    }

}