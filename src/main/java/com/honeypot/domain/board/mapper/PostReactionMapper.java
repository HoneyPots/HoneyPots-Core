package com.honeypot.domain.board.mapper;

import com.honeypot.domain.board.enums.ReactionTarget;
import com.honeypot.domain.board.dto.ReactionDto;
import com.honeypot.domain.board.dto.ReactionRequest;
import com.honeypot.domain.board.entity.PostReaction;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = ReactionTarget.class)
public interface PostReactionMapper {

    @Mapping(target = "reactor.gender", ignore = true)
    @Mapping(target = "reactor.birthday", ignore = true)
    @Mapping(target = "reactor.ageRange", ignore = true)
    @Mapping(target = "reactor.email", ignore = true)
    @Mapping(target = "reactor.createdAt", ignore = true)
    @Mapping(target = "reactor.lastModifiedAt", ignore = true)
    @Mapping(source = "reactionId", target = "id")
    @Mapping(source = "reactor.id", target = "reactor.id")
    @Mapping(target = "post.id",
            expression = "java(reactionDto.getTargetType() == ReactionTarget.POST ? reactionDto.getTargetId() : null)")
    PostReaction toEntity(ReactionDto reactionDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "targetId", target = "post.id")
    @Mapping(source = "reactorId", target = "reactor.id")
    PostReaction toEntity(ReactionRequest dto);

    @InheritInverseConfiguration
    @Mapping(source = "post.id", target = "targetId")
    ReactionDto toDto(PostReaction entity);

}
