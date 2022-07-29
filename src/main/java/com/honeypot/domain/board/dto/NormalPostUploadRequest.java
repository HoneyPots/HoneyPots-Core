package com.honeypot.domain.board.dto;

import com.honeypot.common.validation.groups.InsertContext;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class NormalPostUploadRequest {

    @Length(max = 30)
    private String title;

    @NotEmpty
    @Length(max = 1000)
    private String content;

    @NotNull(groups = InsertContext.class)
    private Long writerId;
}
