package com.honeypot.domain.member.repository;

import com.honeypot.domain.member.entity.Member;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void findById() {
        // Arrange
        Member member = createMember("testNickname");

        // Act
        Optional<Member> result = memberRepository.findById(member.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(member.getId(), result.get().getId());
    }

    @Test
    void isAvailableNickname_NotAvailable() {
        // Arrange
        String nickname = "?";
        createMember(nickname);

        // Act
        boolean result = memberRepository.isAvailableNickname(nickname);

        // Assert
        assertFalse(result);
    }

    @Test
    void isAvailableNickname_Available() {
        // Arrange
        String nickname = "testNickname";

        // Act
        boolean result = memberRepository.isAvailableNickname(nickname);

        // Assert
        assertTrue(result);
    }

    @Test
    void withdrawById() {
        // Arrange
        Member member = createMember("testNickname");

        // Act
        int result = memberRepository.withdrawById(member.getId());

        // Assert
        assertEquals(1, result);
        assertFalse(memberRepository.findById(member.getId()).isPresent());
    }

    private Member createMember(String nickname) {
        Member member = Member.builder()
                .nickname(nickname)
                .build();
        return memberRepository.save(member);
    }

}