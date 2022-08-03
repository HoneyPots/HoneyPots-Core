package com.honeypot.domain.post.mapper;

import com.honeypot.domain.post.dto.NormalPostDto;
import com.honeypot.domain.post.dto.NormalPostUploadRequest;
import com.honeypot.domain.post.entity.NormalPost;
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

    @Mapping(target = "likeReactionCount", ignore = true)
    @Mapping(target = "commentCount", ignore = true)
    @Mapping(source = "id", target = "postId")
    @Mapping(source = "createdAt", target = "uploadedAt")
    NormalPostDto toDto(NormalPost entity);

    List<NormalPostDto> toDto(List<NormalPost> entities);

}
