package com.honeypot.domain.member.service;

import com.honeypot.domain.auth.repository.AuthProviderRepository;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;

@Service
@RequiredArgsConstructor
@Validated
public class MemberWithdrawService {

    private final MemberRepository memberRepository;

    private final AuthProviderRepository authProviderRepository;

    @Transactional
    public boolean withdraw(@NotNull Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(EntityNotFoundException::new);

        memberRepository.withdrawById(memberId);
        authProviderRepository.deleteByMemberId(memberId);

        return true;
    }

}
