package com.honeypot.domain.post.service;

import com.honeypot.common.model.exceptions.InvalidAuthorizationException;
import com.honeypot.domain.file.*;
import com.honeypot.domain.member.dto.WriterDto;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.repository.MemberRepository;
import com.honeypot.domain.post.dto.NormalPostDto;
import com.honeypot.domain.post.dto.NormalPostUploadRequest;
import com.honeypot.domain.post.entity.NormalPost;
import com.honeypot.domain.post.entity.enums.PostType;
import com.honeypot.domain.post.mapper.NormalPostMapper;
import com.honeypot.domain.post.repository.NormalPostQuerydslRepository;
import com.honeypot.domain.post.repository.NormalPostRepository;
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
class NormalPostServiceTest {

    private final NormalPostMapper normalPostMapper = Mappers.getMapper(NormalPostMapper.class);

    @Mock
    private NormalPostQuerydslRepository querydslRepository;

    @Mock
    private NormalPostMapper normalPostMapperMock;

    @Mock
    private NormalPostRepository normalPostRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FileUploadService fileUploadService;

    private NormalPostService normalPostService;

    @BeforeEach
    private void before() {
        this.normalPostService = new NormalPostService(querydslRepository, normalPostMapperMock,
                normalPostRepository, memberRepository, fileUploadService);
    }

    @Test
    void getPostType() {
        // Act & Assert
        PostType result = normalPostService.getPostType();
        assertEquals(PostType.NORMAL, result);
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
        List<NormalPostDto> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(createNormalPostDto(i + 1L, writerDto));
        }

        Page<NormalPostDto> normalPostDtoPage = new PageImpl<>(list, pageable, totalCount);
        when(querydslRepository.findAllPostWithCommentAndReactionCount(pageable, memberId))
                .thenReturn(normalPostDtoPage);

        // Act
        Page<NormalPostDto> result = normalPostService.pageList(pageable, memberId);

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

        Page<NormalPostDto> normalPostDtoPage = new PageImpl<>(new ArrayList<>(), pageable, 0);
        when(querydslRepository.findAllPostWithCommentAndReactionCountByMemberId(pageable, memberId))
                .thenReturn(normalPostDtoPage);

        // Act
        Page<NormalPostDto> result = normalPostService.pageListByMemberId(pageable, memberId);

        // Assert
        assertNotNull(result);
        assertEquals(new ArrayList<>(), result.getContent());
    }

    @Test
    void find() {
        // Arrange
        Long postId = 1L;
        WriterDto writer = new WriterDto(1L, "nickname");

        NormalPostDto post = createNormalPostDto(postId, writer);
        when(normalPostRepository.findById(postId)).thenReturn(Optional.of(mock(NormalPost.class)));
        when(querydslRepository.findPostDetailById(postId, writer.getId())).thenReturn(post);

        // Act
        NormalPostDto result = normalPostService.find(postId, writer.getId());

        // Assert
        assertNotNull(result);
        assertEquals(post.getPostId(), result.getPostId());
    }

    @Test
    void find_PostIsNotFound() {
        // Arrange
        Long postId = 1L;
        when(normalPostRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            normalPostService.find(postId, 1L);
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
        NormalPostUploadRequest request = NormalPostUploadRequest.builder()
                .title(title)
                .content(content)
                .writerId(writer.getId())
                .attachedFiles(postFileUploadRequests)
                .build();

        Member member = Member.builder().id(writer.getId()).nickname(writer.getNickname()).build();
        LocalDateTime now = LocalDateTime.now();
        NormalPost created = NormalPost.builder()
                .id(postId)
                .title(title)
                .writer(member)
                .content(content)
                .attachedFiles(new ArrayList<>())
                .createdAt(now)
                .lastModifiedAt(now)
                .build();

        NormalPost entity = normalPostMapper.toEntity(request);
        when(normalPostMapperMock.toEntity(request)).thenReturn(entity);
        when(normalPostRepository.save(entity)).thenReturn(created);
        when(memberRepository.findById(writer.getId())).thenReturn(Optional.of(member));
        NormalPostDto dto = normalPostMapper.toDto(created);
        when(normalPostMapperMock.toDto(created)).thenReturn(dto);

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
        NormalPostDto result = normalPostService.upload(request);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getPostId());
        assertEquals(request.getTitle(), result.getTitle());
        assertEquals(request.getContent(), result.getContent());
        assertEquals(request.getWriterId(), result.getWriter().getId());
        assertNotNull(result.getWriter().getNickname());

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
        NormalPostUploadRequest request = NormalPostUploadRequest.builder()
                .title(title)
                .content(content)
                .writerId(writer.getId())
                .build();

        Member member = Member.builder().id(writer.getId()).nickname(writer.getNickname()).build();
        LocalDateTime now = LocalDateTime.now();
        NormalPost created = NormalPost.builder()
                .id(postId)
                .title(title)
                .writer(member)
                .content(content)
                .createdAt(now)
                .lastModifiedAt(now)
                .build();

        NormalPost entity = normalPostMapper.toEntity(request);
        when(normalPostMapperMock.toEntity(request)).thenReturn(entity);
        when(normalPostRepository.save(entity)).thenReturn(created);
        when(memberRepository.findById(writer.getId())).thenReturn(Optional.of(member));
        NormalPostDto dto = normalPostMapper.toDto(created);
        when(normalPostMapperMock.toDto(created)).thenReturn(dto);

        // Act
        NormalPostDto result = normalPostService.upload(request);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getPostId());
        assertEquals(request.getTitle(), result.getTitle());
        assertEquals(request.getContent(), result.getContent());
        assertEquals(request.getWriterId(), result.getWriter().getId());
        assertNotNull(result.getWriter().getNickname());
    }

    @Test
    void update() {
        // Arrange
        Long postId = 1L;
        Member writer = Member.builder().id(1L).nickname("nickname").build();
        List<File> attachedFiles = createAttachFiles(3);
        NormalPost existed = createNormalPostEntity(postId, writer, attachedFiles);

        NormalPostUploadRequest uploadRequest = NormalPostUploadRequest.builder()
                .title(existed.getTitle() + " 수정")
                .content(existed.getContent() + " 수정")
                .writerId(writer.getId())
                .attachedFiles(null)
                .build();

        NormalPost updated = NormalPost.builder()
                .id(existed.getId())
                .title(uploadRequest.getTitle())
                .content(uploadRequest.getContent())
                .writer(existed.getWriter())
                .attachedFiles(existed.getAttachedFiles())
                .createdAt(existed.getCreatedAt())
                .lastModifiedAt(existed.getLastModifiedAt())
                .build();

        when(normalPostRepository.findById(postId)).thenReturn(Optional.of(existed));
        existed.setTitle(uploadRequest.getTitle());
        existed.setContent(uploadRequest.getContent());
        when(normalPostRepository.save(existed)).thenReturn(updated);

        NormalPostDto dto = normalPostMapper.toDto(updated);
        when(normalPostMapperMock.toDto(updated)).thenReturn(dto);

        // Act
        NormalPostDto result = normalPostService.update(postId, uploadRequest);

        // Assert
        assertEquals(uploadRequest.getTitle(), result.getTitle());
        assertEquals(uploadRequest.getContent(), result.getContent());
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
        when(normalPostRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            normalPostService.update(postId, mock(NormalPostUploadRequest.class));
        });
    }

    @Test
    void update_InvalidAuthorization() {
        // Arrange
        Long postId = 1L;
        Long anotherMemberId = 2L;
        Member writer = Member.builder().id(1L).nickname("nickname").build();
        NormalPost existed = createNormalPostEntity(postId, writer, null);
        NormalPostUploadRequest uploadRequest = NormalPostUploadRequest.builder()
                .title(existed.getTitle() + " 수정")
                .content(existed.getContent() + " 수정")
                .writerId(anotherMemberId)
                .attachedFiles(null)
                .build();

        when(normalPostRepository.findById(postId)).thenReturn(Optional.of(existed));

        // Act & Assert
        assertThrows(InvalidAuthorizationException.class, () -> {
            normalPostService.update(postId, uploadRequest);
        });
    }

    @Test
    void delete() {
        // Arrange
        Long postId = 1L;
        Member writer = Member.builder().id(1L).nickname("nickname").build();
        List<File> attachedFiles = createAttachFiles(1);
        NormalPost existed = createNormalPostEntity(postId, writer, attachedFiles);

        when(normalPostRepository.findById(postId)).thenReturn(Optional.of(existed));

        // Act
        normalPostService.delete(postId, writer.getId());

        // Assert
        verify(normalPostRepository, times(1)).delete(existed);
    }

    @Test
    void delete_PostNotFound() {
        // Arrange
        Long postId = 1L;
        Long memberId = 1L;
        when(normalPostRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            normalPostService.delete(postId, memberId);
        });
    }

    @Test
    void delete_InvalidAuthorization() {
        // Arrange
        Long postId = 1L;
        Long anotherMemberId = 2L;
        Member writer = Member.builder().id(1L).nickname("nickname").build();
        NormalPost existed = createNormalPostEntity(postId, writer, null);

        when(normalPostRepository.findById(postId)).thenReturn(Optional.of(existed));

        // Act & Assert
        assertThrows(InvalidAuthorizationException.class, () -> {
            normalPostService.delete(postId, anotherMemberId);
        });
    }

    private NormalPostDto createNormalPostDto(Long id, WriterDto writerDto) {
        LocalDateTime now = LocalDateTime.now();
        return NormalPostDto.builder()
                .postId(id)
                .title("제목" + id)
                .content("내용" + id)
                .writer(writerDto)
                .uploadedAt(now)
                .lastModifiedAt(now)
                .build();
    }

    private NormalPost createNormalPostEntity(Long id, Member writer, List<File> attachedFiles) {
        LocalDateTime now = LocalDateTime.now();
        return NormalPost.builder()
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