package com.honeypot.domain.member.service;

import com.honeypot.domain.member.dto.MemberDto;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.mapper.MemberMapper;
import com.honeypot.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class MemberFindServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberMapper memberMapperMock;

    private MemberFindService memberFindService;

    @BeforeEach
    private void before() {
        this.memberFindService = new MemberFindService(memberRepository, memberMapperMock);
    }

    @Test
    void findById_MemberFound() {
        // Arrange
        Long id = 1L;
        Member member = mock(Member.class);
        when(memberRepository.findById(id)).thenReturn(Optional.of(member));
        MemberDto memberDto = mock(MemberDto.class);
        when(memberMapperMock.toDto(member)).thenReturn(memberDto);

        // Act
        Optional<MemberDto> result = memberFindService.findById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(memberDto, result.get());
    }

    @Test
    void findById_MemberNotFound() {
        // Arrange
        Long id = 1L;
        when(memberRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<MemberDto> result = memberFindService.findById(id);

        // Assert
        assertFalse(result.isPresent());
    }

}