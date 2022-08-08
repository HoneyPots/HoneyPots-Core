package com.honeypot.domain.post.mapper;

import com.honeypot.domain.post.dto.UsedTradePostDto;
import com.honeypot.domain.post.dto.UsedTradePostUploadRequest;
import com.honeypot.domain.post.entity.UsedTradePost;
import com.honeypot.domain.post.entity.enums.TradeStatus;
import com.honeypot.domain.post.entity.enums.TradeType;
import com.honeypot.domain.reaction.entity.enums.ReactionTarget;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", imports = {TradeStatus.class, TradeType.class})
public interface UsedTradePostMapper {

    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "id", source = "postId")
    @Mapping(target = "createdAt", source = "uploadedAt")
    UsedTradePost toEntity(UsedTradePostDto dto);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tradeStatus", expression = "java(TradeStatus.ONGOING)")
    @Mapping(target = "tradeType", expression = "java(TradeType.valueOf(dto.getTradeType()))")
    @Mapping(target = "writer.id", source = "writerId")
    UsedTradePost toEntity(UsedTradePostUploadRequest dto);

    @Mapping(target = "commentCount", ignore = true)
    @Mapping(target = "likeReactionCount", ignore = true)
    @Mapping(target = "isLiked", ignore = true)
    @Mapping(target = "likeReactionId", ignore = true)
    @Mapping(target = "thumbnailImageFile", ignore = true)
    @Mapping(target = "attachedFiles", ignore = true)
    @InheritInverseConfiguration
    UsedTradePostDto toDto(UsedTradePost entity);

    List<UsedTradePostDto> toDto(List<UsedTradePost> entities);

}
