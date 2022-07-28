package com.honeypot.domain.board.mapper;

import com.honeypot.domain.board.dto.NormalPostDto;
import com.honeypot.domain.board.entity.NormalPost;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NormalPostMapper {

    @Mapping(source = "id", target = "postId")
    @Mapping(source = "createdAt", target = "uploadedAt")
    NormalPost toEntity(NormalPostDto dto);

    @InheritInverseConfiguration
    NormalPostDto toDto(NormalPost entity);

}
