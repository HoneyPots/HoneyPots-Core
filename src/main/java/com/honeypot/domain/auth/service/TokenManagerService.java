package com.honeypot.domain.auth.service;

import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;

// TODO this is just stubbing class. should be implemented.
@Service
@RequiredArgsConstructor
@Validated
public class TokenManagerService {

    private final MemberRepository memberRepository;

    public String issueToken(@NotNull Long memberId) {
        Member member = memberRepository
                .findById(memberId)
                .orElseThrow(EntityNotFoundException::new);

        return member.getId() + "_" + member.getNickname();
    }

    public boolean verifyToken(@NotNull String token) {
        return token.split("_").length == 2;
    }

    public Long getUserId(@NotNull String token) {
        return Long.parseLong(token.split("_")[0]);
    }

}
