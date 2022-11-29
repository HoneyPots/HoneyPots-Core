package com.honeypot.domain.member.mapper;

import com.honeypot.domain.member.dto.MemberDto;
import com.honeypot.domain.member.entity.Member;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MemberMapperTest {

    private final MemberMapper mapper = Mappers.getMapper(MemberMapper.class);

    @Test
    void toEntity() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        MemberDto dto = MemberDto.builder()
                .id(1123L)
                .nickname("nickname")
                .createdAt(now)
                .lastModifiedAt(now)
                .build();

        // Act
        Member entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getNickname(), entity.getNickname());
        assertEquals(dto.getCreatedAt(), entity.getCreatedAt());
        assertEquals(dto.getLastModifiedAt(), entity.getLastModifiedAt());
    }

    @Test
    void toDto() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Member entity = Member.builder()
                .id(1123L)
                .nickname("nickname")
                .createdAt(now)
                .lastModifiedAt(now)
                .build();

        // Act
        MemberDto dto = mapper.toDto(entity);

        // Assert
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getNickname(), dto.getNickname());
        assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
        assertEquals(entity.getLastModifiedAt(), dto.getLastModifiedAt());
    }
}