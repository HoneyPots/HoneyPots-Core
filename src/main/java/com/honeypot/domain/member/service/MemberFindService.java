package com.honeypot.domain.member.service;

import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class MemberFindService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Optional<Member> findById(@NotNull Long id) {
        return memberRepository.findById(id);
    }

}
