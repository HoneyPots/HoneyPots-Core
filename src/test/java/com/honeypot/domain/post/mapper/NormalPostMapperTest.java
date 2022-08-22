package com.honeypot.domain.post.mapper;

import com.honeypot.domain.comment.entity.Comment;
import com.honeypot.domain.file.AttachedFileResponse;
import com.honeypot.domain.file.File;
import com.honeypot.domain.file.FileType;
import com.honeypot.domain.file.PostFileUploadRequest;
import com.honeypot.domain.member.dto.WriterDto;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.post.dto.NormalPostDto;
import com.honeypot.domain.post.dto.NormalPostUploadRequest;
import com.honeypot.domain.post.entity.NormalPost;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.io.Writer;
import java.lang.ref.WeakReference;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NormalPostMapperTest {

    private final NormalPostMapper mapper = Mappers.getMapper(NormalPostMapper.class);

    @Test
    void toEntity_WhenDtoIsNull() {
        // Arrange
        NormalPostDto dto = null;

        // Act
        NormalPost entity = mapper.toEntity(dto);

        // Assert
        assertNull(entity);
    }

    @Test
    void toEntity() {
        // Arrange
        WriterDto writerDto = WriterDto.builder()
                .id(11L)
                .nickname("nickname")
                .build();
        List<AttachedFileResponse> attachedFileResponses = new ArrayList<>();
        attachedFileResponses.add(AttachedFileResponse.builder()
                .fileId(1L)
                .fileLocationUrl("http://file-server.com/img/img.png")
                .build());
        attachedFileResponses.add(null);

        NormalPostDto dto = createDtoWithAnyData(1L, writerDto, attachedFileResponses);

        // Act
        NormalPost entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getPostId(), entity.getId());
        assertEquals(dto.getTitle(), entity.getTitle());
        assertEquals(dto.getContent(), entity.getContent());
        assertEquals(dto.getWriter().getId(), entity.getWriter().getId());
        assertEquals(dto.getWriter().getNickname(), entity.getWriter().getNickname());
        assertEquals(dto.getUploadedAt(), entity.getCreatedAt());
        assertEquals(dto.getLastModifiedAt(), entity.getLastModifiedAt());
    }

    @Test
    void toEntity_WriterIsNull_AttachedFileIsNull() {
        // Arrange
        WriterDto writerDto = null;
        List<AttachedFileResponse> attachedFileResponses = null;
        NormalPostDto dto = createDtoWithAnyData(1L, writerDto, attachedFileResponses);

        // Act
        NormalPost entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getPostId(), entity.getId());
        assertEquals(dto.getTitle(), entity.getTitle());
        assertEquals(dto.getContent(), entity.getContent());
        assertNull(entity.getWriter());
        assertEquals(dto.getUploadedAt(), entity.getCreatedAt());
        assertEquals(dto.getLastModifiedAt(), entity.getLastModifiedAt());
    }

    @Test
    void toEntity_FromNormalPostUploadRequest_WhenDtoIsNull() {
        // Arrange
        NormalPostUploadRequest dto = null;

        // Act
        NormalPost entity = mapper.toEntity(dto);

        // Assert
        assertNull(entity);
    }

    @Test
    void toEntity_FromNormalPostUploadRequest() {
        // Arrange
        List<PostFileUploadRequest> postFileUploadRequests = new ArrayList<>();
        postFileUploadRequests.add(PostFileUploadRequest.builder()
                .fileId(1L)
                .willBeUploaded(true)
                .linkPostId(12L)
                .build());
        postFileUploadRequests.add(null);
        NormalPostUploadRequest dto = NormalPostUploadRequest.builder()
                .title("title")
                .content("content")
                .writerId(1L)
                .attachedFiles(postFileUploadRequests)
                .build();

        // Act
        NormalPost entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getTitle(), entity.getTitle());
        assertEquals(dto.getContent(), entity.getContent());
        assertEquals(dto.getWriterId(), entity.getWriter().getId());
    }

    @Test
    void toDto_WhenEntityIsNull() {
        // Arrange
        NormalPost entity = null;

        // Act
        NormalPostDto dto = mapper.toDto(entity);

        // Assert
        assertNull(dto);
    }

    @Test
    void toDto() {
        // Arrange
        Member writer = Member.builder()
                .id(1L)
                .nickname("nickname")
                .build();
        List<File> attachedFiles = new ArrayList<>();
        attachedFiles.add(File.builder()
                .id(1123L)
                .fileType(FileType.NORMAL_POST_IMAGE)
                .filePath("img/")
                .filename("name,png")
                .originalFilename("original.png")
                .build());
        attachedFiles.add(null);

        NormalPost entity = createEntityWithAnyData(1L, writer, attachedFiles);

        // Act
        NormalPostDto dto = mapper.toDto(entity);

        // Assert
        assertEquals(entity.getId(), dto.getPostId());
        assertEquals(entity.getTitle(), dto.getTitle());
        assertEquals(entity.getContent(), dto.getContent());
        assertEquals(entity.getWriter().getId(), dto.getWriter().getId());
        assertEquals(entity.getWriter().getNickname(), dto.getWriter().getNickname());
        assertNull(dto.getCommentCount());
        assertEquals(entity.getCreatedAt(), dto.getUploadedAt());
        assertEquals(entity.getLastModifiedAt(), dto.getUploadedAt());
    }

    @Test
    void toDto_EntityListToDtoList_WhenEntityListIsNull() {
        // Arrange
        List<NormalPost> entities = null;

        // Act
        List<NormalPostDto> dtoList = mapper.toDto(entities);

        // Assert
        assertNull(dtoList);
    }

    @Test
    void toDto_EntityListToDtoList() {
        // Arrange
        int listSize = 10;
        List<NormalPost> entities = new ArrayList<>();
        for (long i = 0; i < listSize; i++) {
            entities.add(createEntityWithAnyData(i, null, null));
        }

        // Act
        List<NormalPostDto> dtoList = mapper.toDto(entities);

        // Assert
        assertNotNull(dtoList);
        assertEquals(listSize, dtoList.size());
        for (int i = 0; i < listSize; i++) {
            NormalPostDto dto = dtoList.get(i);
            NormalPost entity = entities.get(i);

            assertEquals(entity.getId(), dto.getPostId());
            assertEquals(entity.getTitle(), dto.getTitle());
            assertEquals(entity.getContent(), dto.getContent());
            if (entity.getWriter() != null) {
                assertEquals(entity.getWriter().getId(), dto.getWriter().getId());
                assertEquals(entity.getWriter().getNickname(), dto.getWriter().getNickname());
            } else {
                assertNull(dto.getWriter());
            }
            assertNull(dto.getCommentCount());
            assertEquals(entity.getCreatedAt(), dto.getUploadedAt());
            assertEquals(entity.getLastModifiedAt(), dto.getUploadedAt());
        }
    }

    private NormalPostDto createDtoWithAnyData(Long id, WriterDto writerDto,
                                               List<AttachedFileResponse> attachedFileResponses) {
        LocalDateTime uploadedAt = LocalDateTime.now();
        AttachedFileResponse thumbnail = null;
        if (attachedFileResponses != null && !attachedFileResponses.isEmpty()) {
            thumbnail = attachedFileResponses.get(0);
        }

        return NormalPostDto.builder()
                .postId(id)
                .title("title")
                .content("content")
                .writer(writerDto)
                .thumbnailImageFile(thumbnail)
                .attachedFiles(attachedFileResponses)
                .isLiked(true)
                .likeReactionId(1L)
                .likeReactionCount(10L)
                .commentCount(10L)
                .uploadedAt(uploadedAt)
                .lastModifiedAt(uploadedAt)
                .build();
    }

    private NormalPost createEntityWithAnyData(Long id, Member member, List<File> attachedFiles) {
        LocalDateTime uploadedAt = LocalDateTime.now();
        return NormalPost.builder()
                .id(id)
                .title("title")
                .content("content")
                .writer(member)
                .comments(List.of(Comment.builder().build()))
                .attachedFiles(attachedFiles)
                .createdAt(uploadedAt)
                .lastModifiedAt(uploadedAt)
                .build();
    }

}
