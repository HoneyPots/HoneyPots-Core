package com.honeypot.domain.post.mapper;

import com.honeypot.domain.post.dto.GroupBuyingPostDto;
import com.honeypot.domain.post.dto.GroupBuyingPostUploadRequest;
import com.honeypot.domain.post.entity.GroupBuyingPost;
import com.honeypot.domain.post.entity.enums.GroupBuyingStatus;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", imports = {GroupBuyingStatus.class})
public interface GroupBuyingPostMapper {

    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "id", source = "postId")
    @Mapping(target = "createdAt", source = "uploadedAt")
    GroupBuyingPost toEntity(GroupBuyingPostDto dto);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "groupBuyingStatus", expression = "java(GroupBuyingStatus.valueOf(dto.getGroupBuyingStatus()))")
    @Mapping(target = "writer.id", source = "writerId")
    GroupBuyingPost toEntity(GroupBuyingPostUploadRequest dto);

    @Mapping(target = "commentCount", ignore = true)
    @Mapping(target = "likeReactionCount", ignore = true)
    @Mapping(target = "isLiked", ignore = true)
    @Mapping(target = "likeReactionId", ignore = true)
    @Mapping(target = "thumbnailImageFile", ignore = true)
    @Mapping(target = "attachedFiles", ignore = true)
    @InheritInverseConfiguration
    GroupBuyingPostDto toDto(GroupBuyingPost entity);

    List<GroupBuyingPostDto> toDto(List<GroupBuyingPost> entities);

}
