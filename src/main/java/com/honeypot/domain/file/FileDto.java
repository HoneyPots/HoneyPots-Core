package com.honeypot.domain.file;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class FileDto {

    private Long fileId;

    @NotNull
    private String filename;

    private String originalFilename;

    @NotNull
    private String filePath;

    @NotNull
    private FileType fileType;

    private String presignedUrl;

}
