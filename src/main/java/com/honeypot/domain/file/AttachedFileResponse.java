package com.honeypot.domain.file;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttachedFileResponse {

    private Long fileId;

    private String fileLocationUrl;

}
