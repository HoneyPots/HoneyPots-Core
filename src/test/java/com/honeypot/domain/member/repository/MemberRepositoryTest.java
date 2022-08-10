package com.honeypot.domain.member.repository;

import com.honeypot.domain.member.entity.Member;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void existsByNickname_NotExist() {
        // Arrange
        String nickname = "?";

        // Act
        boolean result = memberRepository.existsByNickname(nickname);

        // Assert
        assertFalse(result);
    }

    @Test
    void existsByNickname_Exist() {
        // Arrange
        String nickname = "testNickname";
        Member member = Member.builder()
                .nickname(nickname)
                .build();
        memberRepository.save(member);

        // Act
        boolean result = memberRepository.existsByNickname(nickname);

        // Assert
        assertTrue(result);
    }

}