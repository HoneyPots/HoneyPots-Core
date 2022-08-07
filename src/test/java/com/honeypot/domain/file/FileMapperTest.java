package com.honeypot.domain.file;

import com.honeypot.domain.post.entity.Post;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FileMapperTest {

    private final FileMapper mapper = Mappers.getMapper(FileMapper.class);


    @Test
    void toEntity() {
        // Arrange
        FileDto dto = FileDto.builder()
                .fileId(1L)
                .filename("filename")
                .originalFilename("originalFilename")
                .filePath("img/test/filepath")
                .fileType(FileType.NORMAL_POST_IMAGE)
                .presignedUrl("presignedUrl")
                .build();

        // Act
        File entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getFileId(), entity.getId());
        assertEquals(dto.getFilename(), entity.getFilename());
        assertEquals(dto.getOriginalFilename(), entity.getOriginalFilename());
        assertEquals(dto.getFilePath(), entity.getFilePath());
        assertEquals(dto.getFileType(), entity.getFileType());
    }

    @Test
    void toDto() {
        // Arrange
        LocalDateTime uploadedAt = LocalDateTime.of(2022, 7, 28, 22, 0, 0);
        File entity = File.builder()
                .id(1L)
                .filename("filename")
                .originalFilename("originalFilename")
                .filePath("filePath")
                .fileType(FileType.NORMAL_POST_IMAGE)
                .post(Post.builder()
                        .id(2L)
                        .build())
                .createdAt(uploadedAt)
                .lastModifiedAt(uploadedAt)
                .build();

        // Act
        FileDto dto = mapper.toDto(entity);

        // Assert
        assertEquals(entity.getId(), dto.getFileId());
        assertEquals(entity.getFilename(), dto.getFilename());
        assertEquals(entity.getOriginalFilename(), dto.getOriginalFilename());
        assertEquals(entity.getFilePath(), dto.getFilePath());
        assertEquals(entity.getFileType(), dto.getFileType());
        assertNull(dto.getPresignedUrl());
    }

}