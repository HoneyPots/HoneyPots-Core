package com.honeypot.domain.post.service;

import com.honeypot.common.model.exceptions.InvalidAuthorizationException;
import com.honeypot.domain.file.*;
import com.honeypot.domain.member.dto.WriterDto;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.repository.MemberRepository;
import com.honeypot.domain.post.dto.GroupBuyingModifyRequest;
import com.honeypot.domain.post.dto.GroupBuyingPostDto;
import com.honeypot.domain.post.dto.GroupBuyingPostUploadRequest;
import com.honeypot.domain.post.entity.GroupBuyingPost;
import com.honeypot.domain.post.entity.enums.GroupBuyingStatus;
import com.honeypot.domain.post.entity.enums.PostType;
import com.honeypot.domain.post.mapper.GroupBuyingPostMapper;
import com.honeypot.domain.post.repository.GroupBuyingPostQuerydslRepository;
import com.honeypot.domain.post.repository.GroupBuyingPostRepository;
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
class GroupBuyingPostServiceTest {

    private final GroupBuyingPostMapper groupBuyingPostMapper = Mappers.getMapper(GroupBuyingPostMapper.class);

    @Mock
    private GroupBuyingPostQuerydslRepository querydslRepository;

    @Mock
    private GroupBuyingPostMapper groupBuyingPostMapperMock;

    @Mock
    private GroupBuyingPostRepository groupBuyingPostRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FileUploadService fileUploadService;

    private GroupBuyingPostService groupBuyingPostService;

    @BeforeEach
    private void before() {
        this.groupBuyingPostService = new GroupBuyingPostService(querydslRepository, groupBuyingPostMapperMock,
                groupBuyingPostRepository, memberRepository, fileUploadService);
    }

    @Test
    void getPostType() {
        // Act & Assert
        PostType result = groupBuyingPostService.getPostType();
        assertEquals(PostType.GROUP_BUYING, result);
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
        List<GroupBuyingPostDto> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(createGroupBuyingPostDto(i + 1L, writerDto));
        }

        Page<GroupBuyingPostDto> normalPostDtoPage = new PageImpl<>(list, pageable, totalCount);
        when(querydslRepository.findAllPostWithCommentAndReactionCount(pageable, memberId))
                .thenReturn(normalPostDtoPage);

        // Act
        Page<GroupBuyingPostDto> result = groupBuyingPostService.pageList(pageable, memberId);

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

        Page<GroupBuyingPostDto> normalPostDtoPage = new PageImpl<>(new ArrayList<>(), pageable, 0);
        when(querydslRepository.findAllPostWithCommentAndReactionCountByMemberId(pageable, memberId))
                .thenReturn(normalPostDtoPage);

        // Act
        Page<GroupBuyingPostDto> result = groupBuyingPostService.pageListByMemberId(pageable, memberId);

        // Assert
        assertNotNull(result);
        assertEquals(new ArrayList<>(), result.getContent());
    }

    @Test
    void find() {
        // Arrange
        Long postId = 1L;
        WriterDto writer = new WriterDto(1L, "nickname");

        GroupBuyingPostDto post = createGroupBuyingPostDto(postId, writer);
        when(groupBuyingPostRepository.findById(postId)).thenReturn(Optional.of(mock(GroupBuyingPost.class)));
        when(querydslRepository.findPostDetailById(postId, writer.getId())).thenReturn(post);

        // Act
        GroupBuyingPostDto result = groupBuyingPostService.find(postId, writer.getId());

        // Assert
        assertNotNull(result);
        assertEquals(post.getPostId(), result.getPostId());
    }

    @Test
    void find_PostIsNotFound() {
        // Arrange
        Long postId = 1L;
        when(groupBuyingPostRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            groupBuyingPostService.find(postId, 1L);
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
        GroupBuyingPostUploadRequest request = GroupBuyingPostUploadRequest.builder()
                .title(title)
                .content(content)
                .writerId(writer.getId())
                .attachedFiles(postFileUploadRequests)
                .category("한식")
                .groupBuyingStatus(GroupBuyingStatus.ONGOING.toString())
                .chatRoomLink("https://open.kakao/example")
                .deadline(LocalDateTime.now())
                .build();

        Member member = Member.builder().id(writer.getId()).nickname(writer.getNickname()).build();
        LocalDateTime now = LocalDateTime.now();
        GroupBuyingPost created = GroupBuyingPost.builder()
                .id(postId)
                .title(title)
                .writer(member)
                .content(content)
                .attachedFiles(new ArrayList<>())
                .category(request.getCategory())
                .groupBuyingStatus(GroupBuyingStatus.valueOf(request.getGroupBuyingStatus()))
                .chatRoomLink(request.getChatRoomLink())
                .deadline(request.getDeadline())
                .createdAt(now)
                .lastModifiedAt(now)
                .build();

        GroupBuyingPost entity = groupBuyingPostMapper.toEntity(request);
        when(groupBuyingPostMapperMock.toEntity(request)).thenReturn(entity);
        when(groupBuyingPostRepository.save(entity)).thenReturn(created);
        when(memberRepository.findById(writer.getId())).thenReturn(Optional.of(member));
        GroupBuyingPostDto dto = groupBuyingPostMapper.toDto(created);
        when(groupBuyingPostMapperMock.toDto(created)).thenReturn(dto);

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
        GroupBuyingPostDto result = groupBuyingPostService.upload(request);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getPostId());
        assertEquals(request.getTitle(), result.getTitle());
        assertEquals(request.getContent(), result.getContent());
        assertEquals(request.getWriterId(), result.getWriter().getId());
        assertNotNull(result.getWriter().getNickname());
        assertEquals(request.getCategory(), result.getCategory());
        assertEquals(request.getGroupBuyingStatus(), result.getGroupBuyingStatus().name());
        assertEquals(request.getChatRoomLink(), result.getChatRoomLink());
        assertEquals(request.getDeadline(), result.getDeadline());

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
        GroupBuyingPostUploadRequest request = GroupBuyingPostUploadRequest.builder()
                .title(title)
                .content(content)
                .writerId(writer.getId())
                .category("한식")
                .groupBuyingStatus(GroupBuyingStatus.ONGOING.toString())
                .chatRoomLink("https://open.kakao/example")
                .deadline(LocalDateTime.now())
                .build();

        Member member = Member.builder().id(writer.getId()).nickname(writer.getNickname()).build();
        LocalDateTime now = LocalDateTime.now();
        GroupBuyingPost created = GroupBuyingPost.builder()
                .id(postId)
                .title(title)
                .writer(member)
                .content(content)
                .category(request.getCategory())
                .groupBuyingStatus(GroupBuyingStatus.valueOf(request.getGroupBuyingStatus()))
                .chatRoomLink(request.getChatRoomLink())
                .deadline(request.getDeadline())
                .createdAt(now)
                .lastModifiedAt(now)
                .build();

        GroupBuyingPost entity = groupBuyingPostMapper.toEntity(request);
        when(groupBuyingPostMapperMock.toEntity(request)).thenReturn(entity);
        when(groupBuyingPostRepository.save(entity)).thenReturn(created);
        when(memberRepository.findById(writer.getId())).thenReturn(Optional.of(member));
        GroupBuyingPostDto dto = groupBuyingPostMapper.toDto(created);
        when(groupBuyingPostMapperMock.toDto(created)).thenReturn(dto);

        // Act
        GroupBuyingPostDto result = groupBuyingPostService.upload(request);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getPostId());
        assertEquals(request.getTitle(), result.getTitle());
        assertEquals(request.getContent(), result.getContent());
        assertEquals(request.getWriterId(), result.getWriter().getId());
        assertNotNull(result.getWriter().getNickname());
        assertEquals(request.getCategory(), result.getCategory());
        assertEquals(request.getGroupBuyingStatus(), result.getGroupBuyingStatus().name());
        assertEquals(request.getChatRoomLink(), result.getChatRoomLink());
        assertEquals(request.getDeadline(), result.getDeadline());
    }

    @Test
    void update() {
        // Arrange
        Long postId = 1L;
        Member writer = Member.builder().id(1L).nickname("nickname").build();
        List<File> attachedFiles = createAttachFiles(3);
        GroupBuyingPost existed = createGroupBuyingPostEntity(postId, writer, attachedFiles);

        GroupBuyingPostUploadRequest uploadRequest = GroupBuyingPostUploadRequest.builder()
                .title(existed.getTitle() + " 수정")
                .content(existed.getContent() + " 수정")
                .writerId(writer.getId())
                .attachedFiles(null)
                .category("한식")
                .groupBuyingStatus(GroupBuyingStatus.ONGOING.toString())
                .chatRoomLink("https://open.kakao/example")
                .deadline(LocalDateTime.now())
                .build();

        GroupBuyingPost updated = GroupBuyingPost.builder()
                .id(existed.getId())
                .title(uploadRequest.getTitle())
                .content(uploadRequest.getContent())
                .writer(existed.getWriter())
                .attachedFiles(existed.getAttachedFiles())
                .category(uploadRequest.getCategory())
                .groupBuyingStatus(GroupBuyingStatus.valueOf(uploadRequest.getGroupBuyingStatus()))
                .chatRoomLink(uploadRequest.getChatRoomLink())
                .deadline(uploadRequest.getDeadline())
                .createdAt(existed.getCreatedAt())
                .lastModifiedAt(existed.getLastModifiedAt())
                .build();

        when(groupBuyingPostRepository.findById(postId)).thenReturn(Optional.of(existed));
        existed.setTitle(uploadRequest.getTitle());
        existed.setContent(uploadRequest.getContent());
        when(groupBuyingPostRepository.save(existed)).thenReturn(updated);

        GroupBuyingPostDto dto = groupBuyingPostMapper.toDto(updated);
        when(groupBuyingPostMapperMock.toDto(updated)).thenReturn(dto);

        // Act
        GroupBuyingPostDto result = groupBuyingPostService.update(postId, uploadRequest);

        // Assert
        assertEquals(uploadRequest.getTitle(), result.getTitle());
        assertEquals(uploadRequest.getContent(), result.getContent());
        assertEquals(uploadRequest.getCategory(), result.getCategory());
        assertEquals(uploadRequest.getGroupBuyingStatus(), result.getGroupBuyingStatus().name());
        assertEquals(uploadRequest.getChatRoomLink(), result.getChatRoomLink());
        assertEquals(uploadRequest.getDeadline(), result.getDeadline());

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
        when(groupBuyingPostRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            groupBuyingPostService.update(postId, mock(GroupBuyingPostUploadRequest.class));
        });
    }

    @Test
    void update_InvalidAuthorization() {
        // Arrange
        Long postId = 1L;
        Long anotherMemberId = 2L;
        Member writer = Member.builder().id(1L).nickname("nickname").build();
        GroupBuyingPost existed = createGroupBuyingPostEntity(postId, writer, null);
        GroupBuyingPostUploadRequest uploadRequest = GroupBuyingPostUploadRequest.builder()
                .title(existed.getTitle() + " 수정")
                .content(existed.getContent() + " 수정")
                .writerId(anotherMemberId)
                .attachedFiles(null)
                .build();

        when(groupBuyingPostRepository.findById(postId)).thenReturn(Optional.of(existed));

        // Act & Assert
        assertThrows(InvalidAuthorizationException.class, () -> {
            groupBuyingPostService.update(postId, uploadRequest);
        });
    }

    @Test
    void updateGroupBuyingStatus() {
        // Arrange
        Long postId = 1L;
        Member writer = Member.builder().id(1L).nickname("nickname").build();
        GroupBuyingPost existed = createGroupBuyingPostEntity(postId, writer, null);
        GroupBuyingModifyRequest request = GroupBuyingModifyRequest.builder()
                .groupBuyingStatus(GroupBuyingStatus.COMPLETE.name())
                .writerId(writer.getId())
                .build();

        GroupBuyingPost updated = GroupBuyingPost.builder()
                .id(existed.getId())
                .title(existed.getTitle())
                .content(existed.getContent())
                .writer(existed.getWriter())
                .attachedFiles(existed.getAttachedFiles())
                .category(existed.getCategory())
                .groupBuyingStatus(GroupBuyingStatus.valueOf(request.getGroupBuyingStatus()))
                .chatRoomLink(existed.getChatRoomLink())
                .deadline(existed.getDeadline())
                .createdAt(existed.getCreatedAt())
                .lastModifiedAt(existed.getLastModifiedAt())
                .build();

        when(groupBuyingPostRepository.findById(postId)).thenReturn(Optional.of(existed));
        existed.setGroupBuyingStatus(GroupBuyingStatus.valueOf(request.getGroupBuyingStatus()));
        when(groupBuyingPostRepository.save(existed)).thenReturn(updated);
        GroupBuyingPostDto dto = groupBuyingPostMapper.toDto(updated);
        when(groupBuyingPostMapperMock.toDto(updated)).thenReturn(dto);

        // Act
        GroupBuyingPostDto result = groupBuyingPostService.updateTradeStatus(postId, request);

        // Assert
        assertEquals(GroupBuyingStatus.valueOf(request.getGroupBuyingStatus()), result.getGroupBuyingStatus());
    }

    @Test
    void updateTradeStatus_PostNotFound() {
        // Arrange
        Long postId = 1L;
        when(groupBuyingPostRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            groupBuyingPostService.updateTradeStatus(postId, mock(GroupBuyingModifyRequest.class));
        });
    }

    @Test
    void updateTradeStatus_InvalidAuthorization() {
        // Arrange
        Long postId = 1L;
        Long anotherMemberId = 2L;
        Member writer = Member.builder().id(1L).nickname("nickname").build();
        GroupBuyingPost existed = createGroupBuyingPostEntity(postId, writer, null);
        GroupBuyingModifyRequest request = GroupBuyingModifyRequest.builder()
                .groupBuyingStatus(GroupBuyingStatus.COMPLETE.name())
                .writerId(anotherMemberId)
                .build();

        when(groupBuyingPostRepository.findById(postId)).thenReturn(Optional.of(existed));

        // Act & Assert
        assertThrows(InvalidAuthorizationException.class, () -> {
            groupBuyingPostService.updateTradeStatus(postId, request);
        });
    }

    @Test
    void delete() {
        // Arrange
        Long postId = 1L;
        Member writer = Member.builder().id(1L).nickname("nickname").build();
        List<File> attachedFiles = createAttachFiles(1);
        GroupBuyingPost existed = createGroupBuyingPostEntity(postId, writer, attachedFiles);

        when(groupBuyingPostRepository.findById(postId)).thenReturn(Optional.of(existed));

        // Act
        groupBuyingPostService.delete(postId, writer.getId());

        // Assert
        verify(groupBuyingPostRepository, times(1)).delete(existed);
    }

    @Test
    void delete_PostNotFound() {
        // Arrange
        Long postId = 1L;
        Long memberId = 1L;
        when(groupBuyingPostRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            groupBuyingPostService.delete(postId, memberId);
        });
    }

    @Test
    void delete_InvalidAuthorization() {
        // Arrange
        Long postId = 1L;
        Long anotherMemberId = 2L;
        Member writer = Member.builder().id(1L).nickname("nickname").build();
        GroupBuyingPost existed = createGroupBuyingPostEntity(postId, writer, null);

        when(groupBuyingPostRepository.findById(postId)).thenReturn(Optional.of(existed));

        // Act & Assert
        assertThrows(InvalidAuthorizationException.class, () -> {
            groupBuyingPostService.delete(postId, anotherMemberId);
        });
    }

    private GroupBuyingPostDto createGroupBuyingPostDto(Long id, WriterDto writerDto) {
        LocalDateTime now = LocalDateTime.now();
        return GroupBuyingPostDto.builder()
                .postId(id)
                .title("제목" + id)
                .content("내용" + id)
                .writer(writerDto)
                .uploadedAt(now)
                .lastModifiedAt(now)
                .build();
    }

    private GroupBuyingPost createGroupBuyingPostEntity(Long id, Member writer, List<File> attachedFiles) {
        LocalDateTime now = LocalDateTime.now();
        return GroupBuyingPost.builder()
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