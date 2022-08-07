package com.honeypot.domain.file;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// TODO write test code
@Mapper(componentModel = "spring")
public interface FileMapper {

    @Mapping(target = "post", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    @Mapping(target = "id", source = "fileId")
    @Mapping(target = "filename", source = "filename")
    @Mapping(target = "originalFilename", source = "originalFilename")
    @Mapping(target = "filePath", source = "filePath")
    @Mapping(target = "fileType", source = "fileType")
    File toEntity(FileDto dto);

    @Mapping(target = "presignedUrl", ignore = true)
    @InheritInverseConfiguration
    FileDto toDto(File entity);

}