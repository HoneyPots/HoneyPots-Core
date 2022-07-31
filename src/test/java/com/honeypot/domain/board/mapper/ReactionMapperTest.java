package com.honeypot.domain.board.mapper;

import com.honeypot.domain.board.dto.ReactionDto;
import com.honeypot.domain.board.dto.ReactionRequest;
import com.honeypot.domain.board.entity.CommentReaction;
import com.honeypot.domain.board.entity.PostReaction;
import com.honeypot.domain.board.entity.Reaction;
import com.honeypot.domain.board.enums.ReactionTarget;
import com.honeypot.domain.board.enums.ReactionType;
import com.honeypot.domain.member.dto.ReactorDto;
import com.honeypot.domain.member.entity.Member;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ReactionMapperTest {

    private final ReactionMapper mapper = Mappers.getMapper(ReactionMapper.class);

    @Test
    void toEntity_PostReactionTarget() {
        // Arrange
        ReactionDto dto = ReactionDto.builder()
                .reactionId(1L)
                .reactionType(ReactionType.LIKE)
                .targetType(ReactionTarget.POST)
                .targetId(1L)
                .reactor(ReactorDto.builder()
                        .id(1)
                        .nickname("nickname")
                        .build())
                .build();

        // Act
        Reaction entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getReactionId(), entity.getId());
        assertEquals(dto.getReactionType(), entity.getReactionType());
        assertEquals(dto.getTargetType(), entity.getTargetType());
        assertNull(entity.getCommentId());
        assertEquals(dto.getTargetId(), entity.getPostId());
        assertEquals(dto.getReactor().getId(), entity.getReactor().getId());
        assertEquals(dto.getReactor().getNickname(), entity.getReactor().getNickname());
    }

    @Test
    void toEntity_CommentReactionTarget() {
        // Arrange
        ReactionDto dto = ReactionDto.builder()
                .reactionId(1L)
                .reactionType(ReactionType.LIKE)
                .targetType(ReactionTarget.COMMENT)
                .targetId(1L)
                .reactor(ReactorDto.builder()
                        .id(1)
                        .nickname("nickname")
                        .build())
                .build();

        // Act
        Reaction entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getReactionId(), entity.getId());
        assertEquals(dto.getReactionType(), entity.getReactionType());
        assertEquals(dto.getTargetType(), entity.getTargetType());
        assertEquals(dto.getTargetId(), entity.getCommentId());
        assertNull(entity.getPostId());
        assertEquals(dto.getReactor().getId(), entity.getReactor().getId());
        assertEquals(dto.getReactor().getNickname(), entity.getReactor().getNickname());
    }

    @Test
    void toEntity_FromReactionRequest_PostReactionTarget() {
        // Arrange
        ReactionRequest dto = ReactionRequest.builder()
                .targetId(1L)
                .targetType(ReactionTarget.POST.toString())
                .reactionType(ReactionType.LIKE)
                .reactorId(1L)
                .build();

        // Act
        Reaction entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getReactionType(), entity.getReactionType());
        assertEquals(dto.getTargetType(), entity.getTargetType());
        assertEquals(dto.getTargetId(), entity.getPostId());
        assertNull(entity.getCommentId());
        assertEquals(dto.getReactorId(), entity.getReactor().getId());
    }

    @Test
    void toEntity_FromReactionRequest_CommentReactionTarget() {
        // Arrange
        ReactionRequest dto = ReactionRequest.builder()
                .targetId(1L)
                .targetType(ReactionTarget.COMMENT.toString())
                .reactionType(ReactionType.LIKE)
                .reactorId(1L)
                .build();

        // Act
        Reaction entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getReactionType(), entity.getReactionType());
        assertEquals(dto.getTargetType(), entity.getTargetType());
        assertNull(entity.getPostId());
        assertEquals(dto.getTargetId(), entity.getCommentId());
        assertEquals(dto.getReactorId(), entity.getReactor().getId());
    }

    @Test
    void toDto_PostReactionTarget() {
        // Arrange
        Reaction entity = Reaction.builder()
                .id(1L)
                .reactor(Member.builder()
                        .id(1L)
                        .nickname("nickname")
                        .build())
                .reactionType(ReactionType.LIKE)
                .targetType(ReactionTarget.POST)
                .postId(1L)
                .commentId(null)
                .build();

        // Act
        ReactionDto dto = mapper.toDto(entity);

        // Assert
        assertEquals(entity.getId(), dto.getReactionId());
        assertEquals(entity.getReactor().getId(), dto.getReactor().getId());
        assertEquals(entity.getReactor().getNickname(), dto.getReactor().getNickname());
        assertEquals(entity.getReactionType(), dto.getReactionType());
        assertEquals(entity.getTargetType(), dto.getTargetType());
        assertEquals(entity.getPostId(), dto.getTargetId());
    }

    @Test
    void toDto_CommentReactionTarget() {
        // Arrange
        Reaction entity = Reaction.builder()
                .id(1L)
                .reactor(Member.builder()
                        .id(1L)
                        .nickname("nickname")
                        .build())
                .reactionType(ReactionType.LIKE)
                .targetType(ReactionTarget.COMMENT)
                .postId(null)
                .commentId(1L)
                .build();

        // Act
        ReactionDto dto = mapper.toDto(entity);

        // Assert
        assertEquals(entity.getId(), dto.getReactionId());
        assertEquals(entity.getReactor().getId(), dto.getReactor().getId());
        assertEquals(entity.getReactor().getNickname(), dto.getReactor().getNickname());
        assertEquals(entity.getReactionType(), dto.getReactionType());
        assertEquals(entity.getTargetType(), dto.getTargetType());
        assertEquals(entity.getCommentId(), dto.getTargetId());
    }

    @Test
    void toPostReactionEntity() {
        // Arrange
        ReactionRequest dto = ReactionRequest.builder()
                .targetId(1L)
                .targetType(ReactionTarget.POST.toString())
                .reactionType(ReactionType.LIKE)
                .reactorId(1L)
                .build();

        // Act
        PostReaction entity = mapper.toPostReactionEntity(dto);

        // Assert
        assertEquals(dto.getReactionType(), entity.getReactionType());
        assertEquals(dto.getTargetType(), entity.getTargetType());
        assertEquals(dto.getTargetId(), entity.getPost().getId());
        assertEquals(dto.getTargetId(), entity.getPostId());
        assertNull(entity.getCommentId());
        assertEquals(dto.getReactorId(), entity.getReactor().getId());
    }

    @Test
    void toCommentReactionEntity() {
        // Arrange
        ReactionRequest dto = ReactionRequest.builder()
                .targetId(1L)
                .targetType(ReactionTarget.COMMENT.toString())
                .reactionType(ReactionType.LIKE)
                .reactorId(1L)
                .build();

        // Act
        CommentReaction entity = mapper.toCommentReactionEntity(dto);

        // Assert
        assertEquals(dto.getReactionType(), entity.getReactionType());
        assertEquals(dto.getTargetType(), entity.getTargetType());
        assertEquals(dto.getTargetId(), entity.getComment().getId());
        assertEquals(dto.getTargetId(), entity.getCommentId());
        assertNull(entity.getPostId());
        assertEquals(dto.getReactorId(), entity.getReactor().getId());
    }
}