package com.honeypot.domain.member.service;

import com.honeypot.common.validation.groups.InsertContext;
import com.honeypot.domain.member.dto.NicknameModifyRequest;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@Service
@RequiredArgsConstructor
@Validated
public class MemberNicknameModifyService {

    private final MemberRepository memberRepository;

    @Transactional
    @Validated(InsertContext.class)
    public boolean changeNickname(@Valid NicknameModifyRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(EntityNotFoundException::new);

        String nickname = request.getNickname();
        if (nickname.equals(member.getNickname())) {
            return true;
        }

        if (isAvailableNickname(nickname)) {
            member.setNickname(request.getNickname());
            memberRepository.save(member);
            return true;
        }

        return false;
    }

    @Transactional(readOnly = true)
    public boolean isAvailableNickname(@NotEmpty String nickname) {
        return memberRepository.isAvailableNickname(nickname);
    }

}
