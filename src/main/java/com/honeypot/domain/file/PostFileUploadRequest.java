package com.honeypot.domain.file;

import com.honeypot.common.validation.groups.InsertContext;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class PostFileUploadRequest {

    @NotNull
    private Long fileId;

    @NotNull
    private boolean willBeUploaded;

    @NotNull(groups = InsertContext.class)
    private Long linkPostId;

}
