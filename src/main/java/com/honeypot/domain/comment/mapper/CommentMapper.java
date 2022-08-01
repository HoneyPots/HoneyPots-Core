package com.honeypot.domain.comment.mapper;

import com.honeypot.domain.comment.entity.Comment;
import com.honeypot.domain.comment.dto.CommentDto;
import com.honeypot.domain.comment.dto.CommentUploadRequest;
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
