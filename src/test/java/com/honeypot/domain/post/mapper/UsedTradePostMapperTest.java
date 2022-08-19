package com.honeypot.domain.post.mapper;

import com.honeypot.domain.comment.entity.Comment;
import com.honeypot.domain.file.AttachedFileResponse;
import com.honeypot.domain.file.File;
import com.honeypot.domain.file.FileType;
import com.honeypot.domain.file.PostFileUploadRequest;
import com.honeypot.domain.member.dto.WriterDto;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.post.dto.UsedTradePostDto;
import com.honeypot.domain.post.dto.UsedTradePostUploadRequest;
import com.honeypot.domain.post.entity.UsedTradePost;
import com.honeypot.domain.post.entity.enums.TradeStatus;
import com.honeypot.domain.post.entity.enums.TradeType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsedTradePostMapperTest {

    private final UsedTradePostMapper mapper = Mappers.getMapper(UsedTradePostMapper.class);

    @Test
    void toEntity_WhenDtoIsNull() {
        // Arrange
        UsedTradePostDto dto = null;

        // Act
        UsedTradePost entity = mapper.toEntity(dto);

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
        UsedTradePostDto dto = createDtoWithAnyData(11L, writerDto, attachedFileResponses);

        // Act
        UsedTradePost entity = mapper.toEntity(dto);

        // Assert
        assertEntity(dto, entity);
    }

    @Test
    void toEntity_WhenWriterDtoAndAttachFileListIsNull() {
        // Arrange
        WriterDto writerDto = null;
        List<AttachedFileResponse> attachedFileResponses = null;
        UsedTradePostDto dto = createDtoWithAnyData(11L, writerDto, attachedFileResponses);

        // Act
        UsedTradePost entity = mapper.toEntity(dto);

        // Assert
        assertEntity(dto, entity);
    }

    @Test
    void toEntity_FromUsedTradePostUploadRequest_WhenDtoIsNull() {
        // Arrange
        UsedTradePostUploadRequest dto = null;

        // Act
        UsedTradePost entity = mapper.toEntity(dto);

        // Assert
        assertNull(entity);
    }

    @Test
    void toEntity_FromUsedTradePostUploadRequest() {
        // Arrange
        List<PostFileUploadRequest> postFileUploadRequests = new ArrayList<>();
        postFileUploadRequests.add(PostFileUploadRequest.builder()
                .fileId(1L)
                .willBeUploaded(true)
                .linkPostId(12L)
                .build());
        postFileUploadRequests.add(null);
        UsedTradePostUploadRequest dto = UsedTradePostUploadRequest.builder()
                .title("title")
                .content("content")
                .writerId(1L)
                .attachedFiles(postFileUploadRequests)
                .goodsPrice(20000)
                .tradeStatus(TradeStatus.COMPLETE.toString())
                .tradeType(TradeType.BUY.toString())
                .chatRoomLink("chatRoomLink")
                .build();

        // Act
        UsedTradePost entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getTitle(), entity.getTitle());
        assertEquals(dto.getContent(), entity.getContent());
        assertEquals(dto.getWriterId(), entity.getWriter().getId());
        assertEquals(dto.getGoodsPrice(), entity.getGoodsPrice());
        assertEquals(dto.getTradeType(), entity.getTradeType().toString());
        assertEquals(dto.getTradeStatus(), entity.getTradeStatus().toString());
    }

    @Test
    void toDto_WhenEntityIsNull() {
        // Arrange
        UsedTradePost entity = null;

        // Act
        UsedTradePostDto dto = mapper.toDto(entity);

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
        UsedTradePost entity = createEntityWithAnyData(1L, writer, attachedFiles);

        // Act
        UsedTradePostDto dto = mapper.toDto(entity);

        // Assert
        assertDto(entity, dto);
    }

    @Test
    void toDtoList_FromEntityList_WhenEntityListIsNull() {
        // Arrange
        List<UsedTradePost> entities = null;

        // Act
        List<UsedTradePostDto> dtoList = mapper.toDto(entities);

        // Assert
        assertNull(dtoList);
    }

    @Test
    void toDtoList_FromEntityList() {
        // Arrange
        int listSize = 10;
        List<UsedTradePost> entities = new ArrayList<>();
        for (long i = 0; i < listSize; i++) {
            entities.add(createEntityWithAnyData(i, null, null));
        }

        // Act
        List<UsedTradePostDto> dtoList = mapper.toDto(entities);

        // Assert
        assertNotNull(dtoList);
        assertEquals(listSize, dtoList.size());
        for (int i = 0; i < listSize; i++) {
            UsedTradePost entity = entities.get(i);
            UsedTradePostDto dto = dtoList.get(i);
            assertDto(entity, dto);
        }
    }

    private UsedTradePost createEntityWithAnyData(Long id, Member writer, List<File> attachedFiles) {
        LocalDateTime uploadedAt = LocalDateTime.now();
        return UsedTradePost.builder()
                .id(id)
                .title("title")
                .content("content")
                .writer(writer)
                .comments(List.of(Comment.builder().build()))
                .attachedFiles(attachedFiles)
                .createdAt(uploadedAt)
                .lastModifiedAt(uploadedAt)

                .goodsPrice(15000)
                .tradeType(TradeType.SELL)
                .tradeStatus(TradeStatus.COMPLETE)
                .chatRoomLink("https://example.com/adfsafsdca")
                .build();
    }

    private void assertDto(UsedTradePost entity, UsedTradePostDto dto) {
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
        assertEquals(entity.getGoodsPrice(), dto.getGoodsPrice());
        assertEquals(entity.getTradeStatus(), dto.getTradeStatus());
        assertEquals(entity.getTradeType(), dto.getTradeType());
        assertEquals(entity.getChatRoomLink(), dto.getChatRoomLink());
    }

    private UsedTradePostDto createDtoWithAnyData(Long id, WriterDto writer,
                                                  List<AttachedFileResponse> attachedFileResponses) {
        LocalDateTime uploadedAt = LocalDateTime.now();
        AttachedFileResponse thumbnail = null;
        if (attachedFileResponses != null && !attachedFileResponses.isEmpty()) {
            thumbnail = attachedFileResponses.get(0);
        }

        return UsedTradePostDto.builder()
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
                .goodsPrice(10000)
                .tradeType(TradeType.SELL)
                .tradeStatus(TradeStatus.ONGOING)
                .chatRoomLink(null)
                .build();
    }

    private void assertEntity(UsedTradePostDto dto, UsedTradePost entity) {
        assertEquals(dto.getPostId(), entity.getId());
        assertEquals(dto.getTitle(), entity.getTitle());
        assertEquals(dto.getContent(), entity.getContent());
        if (dto.getWriter() != null) {
            assertEquals(dto.getWriter().getId(), entity.getWriter().getId());
            assertEquals(dto.getWriter().getNickname(), entity.getWriter().getNickname());
        } else {
            assertNull(entity.getWriter());
        }
        assertEquals(dto.getGoodsPrice(), entity.getGoodsPrice());
        assertEquals(dto.getTradeType(), entity.getTradeType());
        assertEquals(dto.getTradeStatus(), entity.getTradeStatus());
        assertEquals(dto.getChatRoomLink(), entity.getChatRoomLink());
        assertEquals(dto.getUploadedAt(), entity.getCreatedAt());
        assertEquals(dto.getLastModifiedAt(), entity.getLastModifiedAt());
    }

}