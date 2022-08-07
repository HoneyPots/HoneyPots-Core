package com.honeypot.domain.file;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class FileUploadRequest {

    @NotNull
    private String filename;

    @NotNull
    private FileType fileType;

}
