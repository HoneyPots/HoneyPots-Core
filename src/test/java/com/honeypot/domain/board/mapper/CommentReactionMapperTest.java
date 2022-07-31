package com.honeypot.domain.board.mapper;

import com.honeypot.domain.board.enums.ReactionTarget;
import com.honeypot.domain.board.dto.ReactionDto;
import com.honeypot.domain.board.dto.ReactionRequest;
import com.honeypot.domain.board.entity.Comment;
import com.honeypot.domain.board.entity.CommentReaction;
import com.honeypot.domain.member.dto.ReactorDto;
import com.honeypot.domain.member.entity.Member;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentReactionMapperTest {

    private final CommentReactionMapper mapper = Mappers.getMapper(CommentReactionMapper.class);

    @Test
    void toEntity() {
        // Arrange
        ReactionDto dto = ReactionDto.builder()
                .reactionId(1L)
                .reactionType("LIKE")
                .targetType(ReactionTarget.COMMENT)
                .targetId(1L)
                .reactor(ReactorDto.builder()
                        .id(1)
                        .nickname("nickname")
                        .build())
                .build();

        // Act
        CommentReaction entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getReactionId(), entity.getId());
        assertEquals(dto.getReactionType(), entity.getReactionType());
        assertEquals(dto.getTargetType(), entity.getTargetType());
        assertEquals(dto.getTargetId(), entity.getComment().getId());
        assertEquals(dto.getReactor().getId(), entity.getReactor().getId());
        assertEquals(dto.getReactor().getNickname(), entity.getReactor().getNickname());
    }

    @Test
    void toEntityFromReactionRequest() {
        // Arrange
        ReactionRequest dto = ReactionRequest.builder()
                .targetId(1L)
                .targetType(ReactionTarget.COMMENT.toString())
                .reactionType("LIKE")
                .reactorId(1L)
                .build();

        // Act
        CommentReaction entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getReactionType(), entity.getReactionType());
        assertEquals(dto.getTargetType(), entity.getTargetType());
        assertEquals(dto.getTargetId(), entity.getComment().getId());
        assertEquals(dto.getReactorId(), entity.getReactor().getId());
    }

    @Test
    void toDto() {
        // Arrange
        CommentReaction entity = CommentReaction.builder()
                .id(1L)
                .reactor(Member.builder()
                        .id(1L)
                        .nickname("nickname")
                        .build())
                .reactionType("LIKE")
                .targetType(ReactionTarget.COMMENT)
                .comment(Comment.builder()
                        .id(1L)
                        .build())
                .build();

        // Act
        ReactionDto dto = mapper.toDto(entity);

        // Assert
        assertEquals(entity.getId(), dto.getReactionId());
        assertEquals(entity.getReactor().getId(), dto.getReactor().getId());
        assertEquals(entity.getReactor().getNickname(), dto.getReactor().getNickname());
        assertEquals(entity.getReactionType(), dto.getReactionType());
        assertEquals(entity.getTargetType(), dto.getTargetType());
        assertEquals(entity.getComment().getId(), dto.getTargetId());
    }

}