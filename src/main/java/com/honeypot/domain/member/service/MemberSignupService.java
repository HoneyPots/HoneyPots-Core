package com.honeypot.domain.member.service;

import com.honeypot.domain.auth.entity.AuthProvider;
import com.honeypot.domain.auth.entity.enums.AuthProviderType;
import com.honeypot.domain.member.dto.MemberDto;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.mapper.MemberMapper;
import com.honeypot.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberSignupService {

    private final MemberRepository memberRepository;

    private final MemberMapper memberMapper;

    public MemberDto signup() {
        Member member = Member.builder()
                .nickname(createNewNickname())
                .build();

        return memberMapper.toDto(memberRepository.save(member));
    }

    public MemberDto signupWithOAuth(String providerMemberId,
                                     AuthProviderType providerType,
                                     LocalDateTime oAuthConnectDate) {

        Member newMember = Member.builder()
                .nickname(createNewNickname())
                .build();

        AuthProvider authProvider = AuthProvider.builder()
                .member(newMember)
                .providerMemberId(providerMemberId)
                .providerType(providerType)
                .connectDate(oAuthConnectDate)
                .build();

        newMember.setAuthProvider(authProvider);

        return memberMapper.toDto(memberRepository.save(newMember));
    }

    // TODO stub method
    private String createNewNickname() {
        return "헤리움" + Timestamp.valueOf(LocalDateTime.now()).getTime();
    }

}
