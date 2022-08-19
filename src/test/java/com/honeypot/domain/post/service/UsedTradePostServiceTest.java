package com.honeypot.domain.post.service;

import com.honeypot.common.model.exceptions.InvalidAuthorizationException;
import com.honeypot.domain.file.*;
import com.honeypot.domain.member.dto.WriterDto;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.repository.MemberRepository;
import com.honeypot.domain.post.dto.UsedTradeModifyRequest;
import com.honeypot.domain.post.dto.UsedTradePostDto;
import com.honeypot.domain.post.dto.UsedTradePostUploadRequest;
import com.honeypot.domain.post.entity.UsedTradePost;
import com.honeypot.domain.post.entity.enums.PostType;
import com.honeypot.domain.post.entity.enums.TradeStatus;
import com.honeypot.domain.post.entity.enums.TradeType;
import com.honeypot.domain.post.mapper.UsedTradePostMapper;
import com.honeypot.domain.post.repository.UsedTradePostQuerydslRepository;
import com.honeypot.domain.post.repository.UsedTradePostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class UsedTradePostServiceTest {

    private final UsedTradePostMapper usedTradePostMapper = Mappers.getMapper(UsedTradePostMapper.class);

    @Mock
    private UsedTradePostQuerydslRepository querydslRepository;

    @Mock
    private UsedTradePostMapper usedTradePostMapperMock;

    @Mock
    private UsedTradePostRepository usedTradePostRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FileUploadService fileUploadService;

    private UsedTradePostService usedTradePostService;

    @BeforeEach
    private void before() {
        this.usedTradePostService = new UsedTradePostService(querydslRepository, usedTradePostMapperMock,
                usedTradePostRepository, memberRepository, fileUploadService);
    }

    @Test
    void getPostType() {
        // Act & Assert
        PostType result = usedTradePostService.getPostType();
        assertEquals(PostType.USED_TRADE, result);
    }

    @Test
    void pageList() {
        // Arrange
        int page = 0;
        int size = 10;
        int totalCount = 15;

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
        Long memberId = 1L;

        WriterDto writerDto = new WriterDto(1L, "nickname");
        List<UsedTradePostDto> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(createUsedTradePostDto(i + 1L, writerDto));
        }

        Page<UsedTradePostDto> normalPostDtoPage = new PageImpl<>(list, pageable, totalCount);
        when(querydslRepository.findAllPostWithCommentAndReactionCount(pageable, memberId))
                .thenReturn(normalPostDtoPage);

        // Act
        Page<UsedTradePostDto> result = usedTradePostService.pageList(pageable, memberId);

        // Assert
        assertNotNull(result);
        assertEquals(list, result.getContent());
        assertEquals(size, result.getNumberOfElements());
        assertEquals(totalCount, result.getTotalElements());
    }

    @Test
    void pageListByMemberId() {
        // Arrange
        Long memberId = 1L;

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createdAt")));

        Page<UsedTradePostDto> normalPostDtoPage = new PageImpl<>(new ArrayList<>(), pageable, 0);
        when(querydslRepository.findAllPostWithCommentAndReactionCountByMemberId(pageable, memberId))
                .thenReturn(normalPostDtoPage);

        // Act
        Page<UsedTradePostDto> result = usedTradePostService.pageListByMemberId(pageable, memberId);

        // Assert
        assertNotNull(result);
        assertEquals(new ArrayList<>(), result.getContent());
    }

    @Test
    void find() {
        // Arrange
        Long postId = 1L;
        WriterDto writer = new WriterDto(1L, "nickname");

        UsedTradePostDto post = createUsedTradePostDto(postId, writer);
        when(usedTradePostRepository.findById(postId)).thenReturn(Optional.of(mock(UsedTradePost.class)));
        when(querydslRepository.findPostDetailById(postId, writer.getId())).thenReturn(post);

        // Act
        UsedTradePostDto result = usedTradePostService.find(postId, writer.getId());

        // Assert
        assertNotNull(result);
        assertEquals(post.getPostId(), result.getPostId());
    }

    @Test
    void find_PostIsNotFound() {
        // Arrange
        Long postId = 1L;
        when(usedTradePostRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            usedTradePostService.find(postId, 1L);
        });
    }

    @Test
    void upload() {
        // Arrange
        Long postId = 1L;
        String title = "title";
        String content = "content";
        WriterDto writer = new WriterDto(1L, "nickname");
        List<PostFileUploadRequest> postFileUploadRequests = createPostFileUploadRequests(2);
        UsedTradePostUploadRequest request = UsedTradePostUploadRequest.builder()
                .title(title)
                .content(content)
                .writerId(writer.getId())
                .attachedFiles(postFileUploadRequests)
                .goodsPrice(10000)
                .tradeType(TradeType.SELL.toString())
                .tradeStatus(TradeStatus.ONGOING.toString())
                .chatRoomLink("https://open.kakao/example")
                .build();

        Member member = Member.builder().id(writer.getId()).nickname(writer.getNickname()).build();
        LocalDateTime now = LocalDateTime.now();
        UsedTradePost created = UsedTradePost.builder()
                .id(postId)
                .title(title)
                .writer(member)
                .content(content)
                .attachedFiles(new ArrayList<>())
                .tradeType(TradeType.valueOf(request.getTradeType()))
                .tradeStatus(TradeStatus.valueOf(request.getTradeStatus()))
                .goodsPrice(request.getGoodsPrice())
                .chatRoomLink(request.getChatRoomLink())
                .createdAt(now)
                .lastModifiedAt(now)
                .build();

        UsedTradePost entity = usedTradePostMapper.toEntity(request);
        when(usedTradePostMapperMock.toEntity(request)).thenReturn(entity);
        when(usedTradePostRepository.save(entity)).thenReturn(created);
        when(memberRepository.findById(writer.getId())).thenReturn(Optional.of(member));
        UsedTradePostDto dto = usedTradePostMapper.toDto(created);
        when(usedTradePostMapperMock.toDto(created)).thenReturn(dto);

        for (PostFileUploadRequest fileUploadRequest : postFileUploadRequests) {
            if (!fileUploadRequest.isWillBeUploaded()) {
                continue;
            }

            AttachedFileResponse response = AttachedFileResponse.builder()
                    .fileId(fileUploadRequest.getFileId())
                    .fileLocationUrl("fileLocation")
                    .build();
            when(fileUploadService.linkFileWithPost(fileUploadRequest)).thenReturn(response);
        }

        // Act
        UsedTradePostDto result = usedTradePostService.upload(request);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getPostId());
        assertEquals(request.getTitle(), result.getTitle());
        assertEquals(request.getContent(), result.getContent());
        assertEquals(request.getWriterId(), result.getWriter().getId());
        assertNotNull(result.getWriter().getNickname());
        assertEquals(request.getGoodsPrice(), result.getGoodsPrice());
        assertEquals(request.getTradeType(), result.getTradeType().toString());
        assertEquals(TradeStatus.ONGOING, result.getTradeStatus());
        assertEquals(request.getChatRoomLink(), result.getChatRoomLink());

        List<PostFileUploadRequest> uploadedFiles = request.getAttachedFiles()
                .stream()
                .filter(PostFileUploadRequest::isWillBeUploaded)
                .toList();

        for (int i = 0; i < uploadedFiles.size(); i++) {
            assertEquals(uploadedFiles.get(i).getFileId(), result.getAttachedFiles().get(i).getFileId());
            assertEquals(result.getPostId(), uploadedFiles.get(i).getLinkPostId());
            assertNotNull(result.getAttachedFiles().get(i).getFileLocationUrl());
        }
    }

    @Test
    void upload_WithoutAttachedFile() {
        // Arrange
        Long postId = 1L;
        String title = "title";
        String content = "content";
        WriterDto writer = new WriterDto(1L, "nickname");
        UsedTradePostUploadRequest request = UsedTradePostUploadRequest.builder()
                .title(title)
                .content(content)
                .writerId(writer.getId())
                .goodsPrice(10000)
                .tradeType(TradeType.SELL.toString())
                .tradeStatus(TradeStatus.ONGOING.toString())
                .chatRoomLink("https://open.kakao/example")
                .build();

        Member member = Member.builder().id(writer.getId()).nickname(writer.getNickname()).build();
        LocalDateTime now = LocalDateTime.now();
        UsedTradePost created = UsedTradePost.builder()
                .id(postId)
                .title(title)
                .writer(member)
                .content(content)
                .tradeType(TradeType.valueOf(request.getTradeType()))
                .tradeStatus(TradeStatus.valueOf(request.getTradeStatus()))
                .goodsPrice(request.getGoodsPrice())
                .chatRoomLink(request.getChatRoomLink())
                .createdAt(now)
                .lastModifiedAt(now)
                .build();

        UsedTradePost entity = usedTradePostMapper.toEntity(request);
        when(usedTradePostMapperMock.toEntity(request)).thenReturn(entity);
        when(usedTradePostRepository.save(entity)).thenReturn(created);
        when(memberRepository.findById(writer.getId())).thenReturn(Optional.of(member));
        UsedTradePostDto dto = usedTradePostMapper.toDto(created);
        when(usedTradePostMapperMock.toDto(created)).thenReturn(dto);

        // Act
        UsedTradePostDto result = usedTradePostService.upload(request);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getPostId());
        assertEquals(request.getTitle(), result.getTitle());
        assertEquals(request.getContent(), result.getContent());
        assertEquals(request.getWriterId(), result.getWriter().getId());
        assertNotNull(result.getWriter().getNickname());
        assertEquals(request.getGoodsPrice(), result.getGoodsPrice());
        assertEquals(request.getTradeType(), result.getTradeType().toString());
        assertEquals(TradeStatus.ONGOING, result.getTradeStatus());
        assertEquals(request.getChatRoomLink(), result.getChatRoomLink());
    }

    @Test
    void update() {
        // Arrange
        Long postId = 1L;
        Member writer = Member.builder().id(1L).nickname("nickname").build();
        List<File> attachedFiles = createAttachFiles(3);
        UsedTradePost existed = createUsedTradePostEntity(postId, writer, attachedFiles);

        UsedTradePostUploadRequest uploadRequest = UsedTradePostUploadRequest.builder()
                .title(existed.getTitle() + " 수정")
                .content(existed.getContent() + " 수정")
                .writerId(writer.getId())
                .attachedFiles(null)
                .goodsPrice(20000)
                .tradeType(TradeType.SELL.toString())
                .tradeStatus(TradeStatus.COMPLETE.toString())
                .chatRoomLink(null)
                .build();

        UsedTradePost updated = UsedTradePost.builder()
                .id(existed.getId())
                .title(uploadRequest.getTitle())
                .content(uploadRequest.getContent())
                .writer(existed.getWriter())
                .attachedFiles(existed.getAttachedFiles())
                .goodsPrice(uploadRequest.getGoodsPrice())
                .tradeType(TradeType.valueOf(uploadRequest.getTradeType()))
                .tradeStatus(TradeStatus.valueOf(uploadRequest.getTradeStatus()))
                .chatRoomLink(uploadRequest.getChatRoomLink())
                .createdAt(existed.getCreatedAt())
                .lastModifiedAt(existed.getLastModifiedAt())
                .build();

        when(usedTradePostRepository.findById(postId)).thenReturn(Optional.of(existed));
        existed.setTitle(uploadRequest.getTitle());
        existed.setContent(uploadRequest.getContent());
        when(usedTradePostRepository.save(existed)).thenReturn(updated);

        UsedTradePostDto dto = usedTradePostMapper.toDto(updated);
        when(usedTradePostMapperMock.toDto(updated)).thenReturn(dto);

        // Act
        UsedTradePostDto result = usedTradePostService.update(postId, uploadRequest);

        // Assert
        assertEquals(uploadRequest.getTitle(), result.getTitle());
        assertEquals(uploadRequest.getContent(), result.getContent());
        assertEquals(uploadRequest.getGoodsPrice(), result.getGoodsPrice());
        assertEquals(uploadRequest.getTradeType(), result.getTradeType().toString());
        assertEquals(uploadRequest.getTradeStatus(), result.getTradeStatus().toString());
        assertEquals(uploadRequest.getChatRoomLink(), result.getChatRoomLink());

//        if (uploadRequest.getAttachedFiles() == null) {
//            assertNull(result.getAttachedFiles());
//        } else {
//            for (int i = 0; i < uploadRequest.getAttachedFiles().size(); i++) {
//                assertEquals(uploadRequest.getAttachedFiles().get(i).getFileId(),
//                        result.getAttachedFiles().get(i).getFileId());
//            }
//        }
    }

    @Test
    void update_PostNotFound() {
        // Arrange
        Long postId = 1L;
        when(usedTradePostRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            usedTradePostService.update(postId, mock(UsedTradePostUploadRequest.class));
        });
    }

    @Test
    void update_InvalidAuthorization() {
        // Arrange
        Long postId = 1L;
        Long anotherMemberId = 2L;
        Member writer = Member.builder().id(1L).nickname("nickname").build();
        UsedTradePost existed = createUsedTradePostEntity(postId, writer, null);
        UsedTradePostUploadRequest uploadRequest = UsedTradePostUploadRequest.builder()
                .title(existed.getTitle() + " 수정")
                .content(existed.getContent() + " 수정")
                .writerId(anotherMemberId)
                .attachedFiles(null)
                .build();

        when(usedTradePostRepository.findById(postId)).thenReturn(Optional.of(existed));

        // Act & Assert
        assertThrows(InvalidAuthorizationException.class, () -> {
            usedTradePostService.update(postId, uploadRequest);
        });
    }

    @Test
    void updateTradeStatus() {
        // Arrange
        Long postId = 1L;
        Member writer = Member.builder().id(1L).nickname("nickname").build();
        UsedTradePost existed = createUsedTradePostEntity(postId, writer, null);
        UsedTradeModifyRequest request = UsedTradeModifyRequest.builder()
                .tradeStatus(TradeStatus.COMPLETE.toString())
                .writerId(writer.getId())
                .build();

        UsedTradePost updated = UsedTradePost.builder()
                .id(existed.getId())
                .title(existed.getTitle())
                .content(existed.getContent())
                .writer(existed.getWriter())
                .attachedFiles(existed.getAttachedFiles())
                .goodsPrice(existed.getGoodsPrice())
                .tradeType(existed.getTradeType())
                .tradeStatus(TradeStatus.valueOf(request.getTradeStatus()))
                .chatRoomLink(existed.getChatRoomLink())
                .createdAt(existed.getCreatedAt())
                .lastModifiedAt(existed.getLastModifiedAt())
                .build();

        when(usedTradePostRepository.findById(postId)).thenReturn(Optional.of(existed));
        existed.setTradeStatus(TradeStatus.valueOf(request.getTradeStatus()));
        when(usedTradePostRepository.save(existed)).thenReturn(updated);
        UsedTradePostDto dto = usedTradePostMapper.toDto(updated);
        when(usedTradePostMapperMock.toDto(updated)).thenReturn(dto);

        // Act
        UsedTradePostDto result = usedTradePostService.updateTradeStatus(postId, request);

        // Assert
        assertEquals(TradeStatus.valueOf(request.getTradeStatus()), result.getTradeStatus());
    }

    @Test
    void updateTradeStatus_PostNotFound() {
        // Arrange
        Long postId = 1L;
        when(usedTradePostRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            usedTradePostService.updateTradeStatus(postId, mock(UsedTradeModifyRequest.class));
        });
    }

    @Test
    void updateTradeStatus_InvalidAuthorization() {
        // Arrange
        Long postId = 1L;
        Long anotherMemberId = 2L;
        Member writer = Member.builder().id(1L).nickname("nickname").build();
        UsedTradePost existed = createUsedTradePostEntity(postId, writer, null);
        UsedTradeModifyRequest request = UsedTradeModifyRequest.builder()
                .tradeStatus(TradeStatus.COMPLETE.name())
                .writerId(anotherMemberId)
                .build();

        when(usedTradePostRepository.findById(postId)).thenReturn(Optional.of(existed));

        // Act & Assert
        assertThrows(InvalidAuthorizationException.class, () -> {
            usedTradePostService.updateTradeStatus(postId, request);
        });
    }

    @Test
    void delete() {
        // Arrange
        Long postId = 1L;
        Member writer = Member.builder().id(1L).nickname("nickname").build();
        List<File> attachedFiles = createAttachFiles(1);
        UsedTradePost existed = createUsedTradePostEntity(postId, writer, attachedFiles);

        when(usedTradePostRepository.findById(postId)).thenReturn(Optional.of(existed));

        // Act
        usedTradePostService.delete(postId, writer.getId());

        // Assert
        verify(usedTradePostRepository, times(1)).delete(existed);
    }

    @Test
    void delete_PostNotFound() {
        // Arrange
        Long postId = 1L;
        Long memberId = 1L;
        when(usedTradePostRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            usedTradePostService.delete(postId, memberId);
        });
    }

    @Test
    void delete_InvalidAuthorization() {
        // Arrange
        Long postId = 1L;
        Long anotherMemberId = 2L;
        Member writer = Member.builder().id(1L).nickname("nickname").build();
        UsedTradePost existed = createUsedTradePostEntity(postId, writer, null);

        when(usedTradePostRepository.findById(postId)).thenReturn(Optional.of(existed));

        // Act & Assert
        assertThrows(InvalidAuthorizationException.class, () -> {
            usedTradePostService.delete(postId, anotherMemberId);
        });
    }

    private UsedTradePostDto createUsedTradePostDto(Long id, WriterDto writerDto) {
        LocalDateTime now = LocalDateTime.now();
        return UsedTradePostDto.builder()
                .postId(id)
                .title("제목" + id)
                .content("내용" + id)
                .writer(writerDto)
                .uploadedAt(now)
                .lastModifiedAt(now)
                .build();
    }

    private UsedTradePost createUsedTradePostEntity(Long id, Member writer, List<File> attachedFiles) {
        LocalDateTime now = LocalDateTime.now();
        return UsedTradePost.builder()
                .id(id)
                .title("제목" + id)
                .content("내용" + id)
                .writer(writer)
                .attachedFiles(attachedFiles)
                .createdAt(now)
                .lastModifiedAt(now)
                .build();
    }

    private List<File> createAttachFiles(int count) {
        List<File> result = new ArrayList<>();
        for (long i = 1; i <= count; i++) {
            String originalFilename = "original.png";
            String filename = Timestamp.valueOf(LocalDateTime.now()).getTime() + "_" + originalFilename;
            File file = File.builder()
                    .id(i)
                    .filePath("/img/normal" + filename)
                    .originalFilename(originalFilename)
                    .filename(filename)
                    .fileType(FileType.NORMAL_POST_IMAGE)
                    .build();

            result.add(file);
        }

        return result;
    }

    private List<PostFileUploadRequest> createPostFileUploadRequests(int count) {
        List<PostFileUploadRequest> result = new ArrayList<>();
        for (long i = 1; i <= count; i++) {
            PostFileUploadRequest request = PostFileUploadRequest.builder()
                    .fileId(i)
                    .willBeUploaded(true)
                    .build();

            result.add(request);
        }

        return result;
    }

}