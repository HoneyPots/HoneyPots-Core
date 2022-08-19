package com.honeypot.domain.post.mapper;

import com.honeypot.domain.comment.entity.Comment;
import com.honeypot.domain.file.AttachedFileResponse;
import com.honeypot.domain.file.File;
import com.honeypot.domain.file.FileType;
import com.honeypot.domain.file.PostFileUploadRequest;
import com.honeypot.domain.member.dto.WriterDto;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.post.dto.GroupBuyingPostDto;
import com.honeypot.domain.post.dto.GroupBuyingPostUploadRequest;
import com.honeypot.domain.post.entity.GroupBuyingPost;
import com.honeypot.domain.post.entity.enums.GroupBuyingStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroupBuyingPostMapperTest {

    private final GroupBuyingPostMapper mapper = Mappers.getMapper(GroupBuyingPostMapper.class);

    @Test
    void toEntity_WhenDtoIsNull() {
        // Arrange
        GroupBuyingPostDto dto = null;

        // Act
        GroupBuyingPost entity = mapper.toEntity(dto);

        // Assert
        assertNull(entity);
    }

    @Test
    void toEntity() {
        // Arrange
        WriterDto writerDto = WriterDto.builder()
                .id(1)
                .nickname("nickname")
                .build();
        List<AttachedFileResponse> attachedFileResponses = new ArrayList<>();
        attachedFileResponses.add(AttachedFileResponse.builder()
                .fileId(1L)
                .fileLocationUrl("http://file-server.com/img/img.png")
                .build());
        attachedFileResponses.add(null);
        GroupBuyingPostDto dto = createDtoWithAnyData(123L, writerDto, attachedFileResponses);

        // Act
        GroupBuyingPost entity = mapper.toEntity(dto);

        // Assert
        assertEntity(dto, entity);
    }

    @Test
    void toEntity_WhenWriterDtoAndAttachFileListIsNull() {
        // Arrange
        WriterDto writerDto = null;
        List<AttachedFileResponse> attachedFileResponses = null;
        GroupBuyingPostDto dto = createDtoWithAnyData(11L, writerDto, attachedFileResponses);

        // Act
        GroupBuyingPost entity = mapper.toEntity(dto);

        // Assert
        assertEntity(dto, entity);
    }

    @Test
    void toEntity_FromGroupBuyingPostUploadRequest_WhenDtoIsNull() {
        // Arrange
        GroupBuyingPostUploadRequest dto = null;

        // Act
        GroupBuyingPost entity = mapper.toEntity(dto);

        // Assert
        assertNull(entity);
    }

    @Test
    void toEntity_FromGroupBuyingPostUploadRequest() {
        // Arrange
        List<PostFileUploadRequest> postFileUploadRequests = new ArrayList<>();
        postFileUploadRequests.add(PostFileUploadRequest.builder()
                .fileId(1L)
                .willBeUploaded(true)
                .linkPostId(12L)
                .build());
        postFileUploadRequests.add(null);
        GroupBuyingPostUploadRequest dto = GroupBuyingPostUploadRequest.builder()
                .title("title")
                .content("content")
                .writerId(1L)
                .attachedFiles(postFileUploadRequests)
                .category("한식")
                .groupBuyingStatus("ONGOING")
                .chatRoomLink("chatRoomLink")
                .deadline(LocalDateTime.now().plus(20000L, ChronoUnit.SECONDS))
                .build();

        // Act
        GroupBuyingPost entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getTitle(), entity.getTitle());
        assertEquals(dto.getContent(), entity.getContent());
        assertEquals(dto.getWriterId(), entity.getWriter().getId());
        assertEquals(dto.getCategory(), entity.getCategory());
        assertEquals(dto.getGroupBuyingStatus(), entity.getGroupBuyingStatus().toString());
    }

    @Test
    void toDto_WhenEntityIsNull() {
        // Arrange
        GroupBuyingPost entity = null;

        // Act
        GroupBuyingPostDto dto = mapper.toDto(entity);

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
        GroupBuyingPost entity = createEntityWithAnyData(1L, writer, attachedFiles);

        // Act
        GroupBuyingPostDto dto = mapper.toDto(entity);

        // Assert
        assertDto(entity, dto);
    }

    @Test
    void toDtoList_FromEntityList_WhenEntityListIsNull() {
        // Arrange
        List<GroupBuyingPost> entities = null;

        // Act
        List<GroupBuyingPostDto> dtoList = mapper.toDto(entities);

        // Assert
        assertNull(dtoList);
    }

    @Test
    void toDtoList_FromEntityList() {
        // Arrange
        int listSize = 10;
        List<GroupBuyingPost> entities = new ArrayList<>();
        for (long i = 0; i < listSize; i++) {
            entities.add(createEntityWithAnyData(i, null, null));
        }

        // Act
        List<GroupBuyingPostDto> dtoList = mapper.toDto(entities);

        // Assert
        assertNotNull(dtoList);
        assertEquals(listSize, dtoList.size());
        for (int i = 0; i < listSize; i++) {
            GroupBuyingPost entity = entities.get(i);
            GroupBuyingPostDto dto = dtoList.get(i);
            assertDto(entity, dto);
        }
    }

    private GroupBuyingPost createEntityWithAnyData(Long id, Member writer, List<File> attachedFiles) {
        LocalDateTime uploadedAt = LocalDateTime.now();
        return GroupBuyingPost.builder()
                .id(id)
                .title("title")
                .content("content")
                .writer(writer)
                .comments(List.of(Comment.builder().build()))
                .attachedFiles(attachedFiles)
                .createdAt(uploadedAt)
                .lastModifiedAt(uploadedAt)

                .category("중식")
                .groupBuyingStatus(GroupBuyingStatus.COMPLETE)
                .chatRoomLink("https://example.com/adfsafsdca")
                .deadline(LocalDateTime.now())
                .build();
    }

    private void assertDto(GroupBuyingPost entity, GroupBuyingPostDto dto) {
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
        assertNull(dto.getLikeReactionCount());
        assertNull(dto.getLikeReactionId());
        assertNull(dto.getIsLiked());
        assertEquals(entity.getCreatedAt(), dto.getUploadedAt());
        assertEquals(entity.getLastModifiedAt(), dto.getUploadedAt());
        assertEquals(entity.getCategory(), dto.getCategory());
        assertEquals(entity.getGroupBuyingStatus(), dto.getGroupBuyingStatus());
        assertEquals(entity.getChatRoomLink(), dto.getChatRoomLink());
        assertEquals(entity.getDeadline(), dto.getDeadline());
    }

    private GroupBuyingPostDto createDtoWithAnyData(Long id, WriterDto writer,
                                                    List<AttachedFileResponse> attachedFileResponses) {
        LocalDateTime uploadedAt = LocalDateTime.now();
        AttachedFileResponse thumbnail = null;
        if (attachedFileResponses != null && !attachedFileResponses.isEmpty()) {
            thumbnail = attachedFileResponses.get(0);
        }

        return GroupBuyingPostDto.builder()
                .postId(id)
                .title("title")
                .content("content")
                .writer(writer)
                .commentCount(10L)
                .likeReactionCount(5L)
                .isLiked(true)
                .likeReactionId(10L)
                .thumbnailImageFile(thumbnail)
                .attachedFiles(attachedFileResponses)
                .uploadedAt(uploadedAt)
                .lastModifiedAt(uploadedAt)

                .category("한식")
                .groupBuyingStatus(GroupBuyingStatus.COMPLETE)
                .chatRoomLink(null)
                .deadline(LocalDateTime.now().plus(20000L, ChronoUnit.SECONDS))
                .build();
    }

    private void assertEntity(GroupBuyingPostDto dto, GroupBuyingPost entity) {
        assertEquals(dto.getPostId(), entity.getId());
        assertEquals(dto.getTitle(), entity.getTitle());
        assertEquals(dto.getContent(), entity.getContent());
        if (dto.getWriter() != null) {
            assertEquals(dto.getWriter().getId(), entity.getWriter().getId());
            assertEquals(dto.getWriter().getNickname(), entity.getWriter().getNickname());
        } else {
            assertNull(entity.getWriter());
        }
        assertEquals(dto.getCategory(), entity.getCategory());
        assertEquals(dto.getGroupBuyingStatus(), entity.getGroupBuyingStatus());
        assertEquals(dto.getChatRoomLink(), entity.getChatRoomLink());
        assertEquals(dto.getDeadline(), entity.getDeadline());
        assertEquals(dto.getUploadedAt(), entity.getCreatedAt());
        assertEquals(dto.getLastModifiedAt(), entity.getLastModifiedAt());
    }

}