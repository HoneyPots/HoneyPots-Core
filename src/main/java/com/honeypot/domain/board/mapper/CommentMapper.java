package com.honeypot.domain.board.mapper;

import com.honeypot.domain.board.dto.CommentDto;
import com.honeypot.domain.board.dto.CommentUploadRequest;
import com.honeypot.domain.board.dto.NormalPostDto;
import com.honeypot.domain.board.dto.NormalPostUploadRequest;
import com.honeypot.domain.board.entity.Comment;
import com.honeypot.domain.board.entity.NormalPost;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "postId", target = "post.id")
    @Mapping(source = "commentId", target = "id")
    @Mapping(source = "writer.id", target = "writer.id")
    Comment toEntity(CommentDto dto);

    @Mapping(source = "postId", target = "post.id")
    @Mapping(source = "writerId", target = "writer.id")
    Comment toEntity(CommentUploadRequest dto);

    @InheritInverseConfiguration
    CommentDto toDto(Comment entity);

    @InheritInverseConfiguration
    List<CommentDto> toDto(List<Comment> entities);

}
