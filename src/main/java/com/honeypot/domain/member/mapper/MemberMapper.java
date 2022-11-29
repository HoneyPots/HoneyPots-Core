package com.honeypot.domain.member.mapper;

import com.honeypot.domain.member.dto.MemberDto;
import com.honeypot.domain.member.entity.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    Member toEntity(MemberDto dto);

    MemberDto toDto(Member entity);

}