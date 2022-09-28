package com.honeypot.domain.comment.service;

import com.honeypot.domain.comment.dto.CommentDto;
import com.honeypot.domain.comment.dto.CommentUploadRequest;
import com.honeypot.domain.comment.entity.Comment;
import com.honeypot.domain.comment.mapper.CommentMapper;
import com.honeypot.domain.comment.repository.CommentRepository;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.service.MemberFindService;
import com.honeypot.domain.notification.dto.CommentNotificationResource;
import com.honeypot.domain.notification.dto.NotificationData;
import com.honeypot.domain.notification.dto.PostNotificationResource;
import com.honeypot.domain.notification.entity.enums.NotificationType;
import com.honeypot.domain.notification.service.NotificationSendService;
import com.honeypot.domain.post.entity.Post;
import com.honeypot.domain.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class CommentServiceTest {

    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Mock
    private CommentMapper commentMapperMock;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberFindService memberFindService;

    @Mock
    private NotificationSendService notificationSendService;

    @Mock
    private CommentService commentService;

    @BeforeEach
    private void before() {
        this.commentService = new CommentService(commentMapperMock, commentRepository,
                postRepository, memberFindService, notificationSendService);
    }

    @Test
    @DisplayName("게시글 작성자와 댓글 작성자가 같은 경우, 푸시 알림 미전송 확인")
    void save_PostWriterEqualsToCommentWriter() {
        // Arrange
        Member commentWriter = Member.builder().id(999L).nickname("게시글 및 댓글 작성자").build();
        Post targetPost = createPost(1231L, commentWriter);

        CommentUploadRequest request = CommentUploadRequest.builder()
                .writerId(commentWriter.getId())
                .postId(targetPost.getId())
                .content("댓글 내용이다.")
                .build();

        Comment created = createCommentFromCommentUploadRequest(1L, request);

        Comment uploadCommentMock = mock(Comment.class);
        when(postRepository.findById(request.getPostId())).thenReturn(Optional.of(targetPost));
        when(commentMapperMock.toEntity(request)).thenReturn(uploadCommentMock);
        when(commentRepository.save(uploadCommentMock)).thenReturn(created);
        when(memberFindService.findById(created.getWriter().getId())).thenReturn(Optional.of(commentWriter));
        CommentDto expected = commentMapper.toDto(created);
        when(commentMapperMock.toDto(created)).thenReturn(expected);

        // Act
        CommentDto result = commentService.save(request);

        // Assert
        assertEquals(expected, result);
        CommentNotificationResource resource = CommentNotificationResource.builder()
                .postResource(PostNotificationResource.builder()
                        .id(targetPost.getId())
                        .type(targetPost.getType())
                        .writer(targetPost.getWriter().getNickname())
                        .build())
                .commentId(result.getCommentId())
                .commenter(result.getWriter().getNickname())
                .build();

        verify(notificationSendService, never()).send(
                targetPost.getWriter().getId(),
                NotificationData.<CommentNotificationResource>builder()
                        .type(NotificationType.COMMENT_TO_MY_POST)
                        .resource(resource)
                        .build()
        );
    }

    @Test
    @DisplayName("게시글 작성자와 댓글 작성자가 다른 경우, 푸시 알림 전송 확인")
    void save_PostWriterIsNotEqualsToCommentWriter() {
        // Arrange
        Member commentWriter = Member.builder().id(999L).nickname("댓글 작성자").build();
        Member postWriter = Member.builder().id(12314L).nickname("게시글 작성자").build();
        Post targetPost = createPost(1231L, postWriter);

        CommentUploadRequest request = CommentUploadRequest.builder()
                .writerId(commentWriter.getId())
                .postId(targetPost.getId())
                .content("댓글 내용이다.")
                .build();

        Comment created = createCommentFromCommentUploadRequest(1L, request);

        Comment uploadCommentMock = mock(Comment.class);
        when(postRepository.findById(request.getPostId())).thenReturn(Optional.of(targetPost));
        when(commentMapperMock.toEntity(request)).thenReturn(uploadCommentMock);
        when(commentRepository.save(uploadCommentMock)).thenReturn(created);
        when(memberFindService.findById(created.getWriter().getId())).thenReturn(Optional.of(commentWriter));
        CommentDto expected = commentMapper.toDto(created);
        when(commentMapperMock.toDto(created)).thenReturn(expected);

        // Act
        CommentDto result = commentService.save(request);

        // Assert
        assertEquals(expected, result);
        CommentNotificationResource resource = CommentNotificationResource.builder()
                .postResource(PostNotificationResource.builder()
                        .id(targetPost.getId())
                        .type(targetPost.getType())
                        .writer(targetPost.getWriter().getNickname())
                        .build())
                .commentId(result.getCommentId())
                .commenter(result.getWriter().getNickname())
                .build();

        verify(notificationSendService, times(1)).send(
                targetPost.getWriter().getId(),
                NotificationData.<CommentNotificationResource>builder()
                        .type(NotificationType.COMMENT_TO_MY_POST)
                        .resource(resource)
                        .build()
        );
    }

    private Post createPost(Long id, Member writer) {
        return Post.builder()
                .id(id)
                .writer(writer)
                .build();
    }

    private Comment createCommentFromCommentUploadRequest(Long id, CommentUploadRequest request) {
        LocalDateTime now = LocalDateTime.now();
        return Comment.builder()
                .id(id)
                .writer(Member.builder().id(request.getWriterId()).build())
                .content(request.getContent())
                .post(Post.builder().id(request.getPostId()).build())
                .createdAt(now)
                .lastModifiedAt(now)
                .build();
    }

}