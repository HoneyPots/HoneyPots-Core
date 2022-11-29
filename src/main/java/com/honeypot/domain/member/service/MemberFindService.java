package com.honeypot.domain.member.service;

import com.honeypot.domain.member.dto.MemberDto;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.mapper.MemberMapper;
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

    private final MemberMapper memberMapper;

    @Transactional(readOnly = true)
    public Optional<MemberDto> findById(@NotNull Long id) {
        Optional<Member> member = memberRepository.findById(id);
        return member.map(memberMapper::toDto);
    }

}
