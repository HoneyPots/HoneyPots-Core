package com.honeypot.domain.board.mapper;

import com.honeypot.domain.board.dto.ReactionDto;
import com.honeypot.domain.board.dto.ReactionRequest;
import com.honeypot.domain.board.entity.CommentReaction;
import com.honeypot.domain.board.entity.PostReaction;
import com.honeypot.domain.board.entity.Reaction;
import com.honeypot.domain.board.enums.ReactionTarget;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = ReactionTarget.class)
public interface ReactionMapper {

    @Mapping(target = "reactor.gender", ignore = true)
    @Mapping(target = "reactor.birthday", ignore = true)
    @Mapping(target = "reactor.ageRange", ignore = true)
    @Mapping(target = "reactor.email", ignore = true)
    @Mapping(target = "reactor.createdAt", ignore = true)
    @Mapping(target = "reactor.lastModifiedAt", ignore = true)
    @Mapping(source = "reactionId", target = "id")
    @Mapping(source = "reactor.id", target = "reactor.id")
    @Mapping(target = "postId",
            expression = "java(dto.getTargetType() == ReactionTarget.POST ? dto.getTargetId() : null)")
    @Mapping(target = "commentId",
            expression = "java(dto.getTargetType() == ReactionTarget.COMMENT ? dto.getTargetId() : null)")
    Reaction toEntity(ReactionDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "reactorId", target = "reactor.id")
    @Mapping(target = "postId",
            expression = "java(dto.getTargetType() == ReactionTarget.POST ? dto.getTargetId() : null)")
    @Mapping(target = "commentId",
            expression = "java(dto.getTargetType() == ReactionTarget.COMMENT ? dto.getTargetId() : null)")
    Reaction toEntity(ReactionRequest dto);

    @Mapping(target = "reactor.gender", ignore = true)
    @Mapping(target = "reactor.birthday", ignore = true)
    @Mapping(target = "reactor.ageRange", ignore = true)
    @Mapping(target = "reactor.email", ignore = true)
    @Mapping(target = "reactor.createdAt", ignore = true)
    @Mapping(target = "reactor.lastModifiedAt", ignore = true)
    @Mapping(source = "reactorId", target = "reactor.id")
    @Mapping(target = "post.id",
            expression = "java(reactionRequest.getTargetType() == ReactionTarget.POST ? reactionRequest.getTargetId() : null)")
    @Mapping(target = "postId",
            expression = "java(reactionRequest.getTargetType() == ReactionTarget.POST ? reactionRequest.getTargetId() : null)")
    PostReaction toPostReactionEntity(ReactionRequest reactionRequest);

    @Mapping(target = "reactor.gender", ignore = true)
    @Mapping(target = "reactor.birthday", ignore = true)
    @Mapping(target = "reactor.ageRange", ignore = true)
    @Mapping(target = "reactor.email", ignore = true)
    @Mapping(target = "reactor.createdAt", ignore = true)
    @Mapping(target = "reactor.lastModifiedAt", ignore = true)
    @Mapping(source = "reactorId", target = "reactor.id")
    @Mapping(target = "comment.id",
            expression = "java(reactionRequest.getTargetType() == ReactionTarget.COMMENT ? reactionRequest.getTargetId() : null)")
    @Mapping(target = "commentId",
            expression = "java(reactionRequest.getTargetType() == ReactionTarget.COMMENT ? reactionRequest.getTargetId() : null)")
    CommentReaction toCommentReactionEntity(ReactionRequest reactionRequest);

    @InheritInverseConfiguration
    @Mapping(target = "alreadyExists", ignore = true)
    @Mapping(target = "targetId",
            expression = "java(entity.getTargetType() == ReactionTarget.POST ? entity.getPostId() : entity.getCommentId())")
    ReactionDto toDto(Reaction entity);

}
