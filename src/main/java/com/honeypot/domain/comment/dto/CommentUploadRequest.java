package com.honeypot.domain.comment.dto;

import com.honeypot.common.validation.groups.InsertContext;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class CommentUploadRequest {

    @NotNull
    private Long postId;

    @NotEmpty
    @Length(max = 200)
    private String content;

    @NotNull(groups = InsertContext.class)
    private Long writerId;

}
