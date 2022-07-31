package com.honeypot.domain.board.mapper;

import com.honeypot.domain.board.enums.ReactionTarget;
import com.honeypot.domain.board.dto.ReactionDto;
import com.honeypot.domain.board.dto.ReactionRequest;
import com.honeypot.domain.board.entity.CommentReaction;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = ReactionTarget.class)
public interface CommentReactionMapper {

    @Mapping(target = "reactor.gender", ignore = true)
    @Mapping(target = "reactor.birthday", ignore = true)
    @Mapping(target = "reactor.ageRange", ignore = true)
    @Mapping(target = "reactor.email", ignore = true)
    @Mapping(target = "reactor.createdAt", ignore = true)
    @Mapping(target = "reactor.lastModifiedAt", ignore = true)
    @Mapping(source = "reactionId", target = "id")
    @Mapping(source = "reactor.id", target = "reactor.id")
    @Mapping(target = "comment.id",
            expression = "java(reactionDto.getTargetType() == ReactionTarget.COMMENT ? reactionDto.getTargetId() : null)")
    CommentReaction toEntity(ReactionDto reactionDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "targetId", target = "comment.id")
    @Mapping(source = "reactorId", target = "reactor.id")
    CommentReaction toEntity(ReactionRequest dto);

    @InheritInverseConfiguration
    @Mapping(source = "comment.id", target = "targetId")
    ReactionDto toDto(CommentReaction entity);

}
