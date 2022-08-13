package com.honeypot.domain.member.service;

import com.honeypot.domain.member.dto.NicknameModifyRequest;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class MemberNicknameModifyServiceTest {

    @Mock
    private MemberRepository memberRepository;

    private MemberNicknameModifyService memberNicknameModifyService;

    @BeforeEach
    private void before() {
        this.memberNicknameModifyService = new MemberNicknameModifyService(memberRepository);
    }

    @Test
    void changeNickname_MemberNotFound() {
        // Arrange
        String nickname = "";
        Long memberId = null;
        NicknameModifyRequest request = NicknameModifyRequest.builder()
                .nickname(nickname)
                .memberId(memberId)
                .build();

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            memberNicknameModifyService.changeNickname(request);
        });
    }

    @Test
    void changeNickname_NicknameIsMine() {
        // Arrange
        String nickname = "nickname";
        Long memberId = 1L;
        NicknameModifyRequest request = NicknameModifyRequest.builder()
                .nickname(nickname)
                .memberId(memberId)
                .build();

        Member member = Member.builder()
                .id(memberId)
                .nickname(nickname)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // Act
        boolean result = memberNicknameModifyService.changeNickname(request);

        // Assert
        assertTrue(result);
        assertEquals(nickname, member.getNickname());
        verify(memberRepository, never()).save(member);
    }

    @Test
    void changeNickname_NicknameIsAlreadyExist() {
        // Arrange
        String afterNickname = "afterNickname";
        Long memberId = 1L;
        NicknameModifyRequest request = NicknameModifyRequest.builder()
                .nickname(afterNickname)
                .memberId(memberId)
                .build();

        Member member = Member.builder()
                .id(memberId)
                .nickname("beforeNickname")
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberRepository.isAvailableNickname(afterNickname)).thenReturn(false);

        // Act
        boolean result = memberNicknameModifyService.changeNickname(request);

        // Assert
        assertFalse(result);
        assertNotEquals(afterNickname, member.getNickname());
        verify(memberRepository, never()).save(member);
    }

    @Test
    void changeNickname_NicknameIsNotExist() {
        // Arrange
        String afterNickname = "afterNickname";
        Long memberId = 1L;
        NicknameModifyRequest request = NicknameModifyRequest.builder()
                .nickname(afterNickname)
                .memberId(memberId)
                .build();

        Member member = Member.builder()
                .id(memberId)
                .nickname("beforeNickname")
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberRepository.isAvailableNickname(afterNickname)).thenReturn(true);

        // Act
        boolean result = memberNicknameModifyService.changeNickname(request);

        // Assert
        assertTrue(result);
        assertEquals(afterNickname, member.getNickname());
        verify(memberRepository, times(1)).save(member);
    }

}