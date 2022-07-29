package com.honeypot.domain.board.mapper;

import com.honeypot.domain.board.dto.NormalPostDto;
import com.honeypot.domain.board.dto.NormalPostUploadRequest;
import com.honeypot.domain.board.entity.NormalPost;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NormalPostMapper {

    @Mapping(source = "postId", target = "id")
    @Mapping(source = "uploadedAt", target = "createdAt")
    NormalPost toEntity(NormalPostDto dto);

    @Mapping(source = "writerId", target = "writer.id")
    NormalPost toEntity(NormalPostUploadRequest dto);

    @Mapping(target = "commentCount",
            expression = "java(entity.getComments() == null ? 0 : entity.getComments().size())")
    @Mapping(source = "id", target = "postId")
    @Mapping(source = "createdAt", target = "uploadedAt")
    NormalPostDto toDto(NormalPost entity);

    List<NormalPostDto> toDto(List<NormalPost> entities);

}
