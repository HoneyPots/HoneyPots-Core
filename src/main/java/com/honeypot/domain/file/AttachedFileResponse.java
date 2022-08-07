package com.honeypot.domain.file;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttachedFileResponse {

    @QueryProjection
    public AttachedFileResponse(Long fileId, String fileLocationUrl) {
        this.fileId = fileId;
        this.fileLocationUrl = fileLocationUrl;
    }

    private Long fileId;

    private String fileLocationUrl;

}
