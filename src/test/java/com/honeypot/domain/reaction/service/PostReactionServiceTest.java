package com.honeypot.domain.reaction.service;

import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.service.MemberFindService;
import com.honeypot.domain.notification.dto.NotificationData;
import com.honeypot.domain.notification.dto.PostNotificationResource;
import com.honeypot.domain.notification.dto.ReactionNotificationResource;
import com.honeypot.domain.notification.entity.enums.NotificationType;
import com.honeypot.domain.notification.service.NotificationSendService;
import com.honeypot.domain.post.entity.Post;
import com.honeypot.domain.post.repository.PostRepository;
import com.honeypot.domain.reaction.dto.ReactionDto;
import com.honeypot.domain.reaction.dto.ReactionRequest;
import com.honeypot.domain.reaction.entity.PostReaction;
import com.honeypot.domain.reaction.entity.enums.ReactionTarget;
import com.honeypot.domain.reaction.entity.enums.ReactionType;
import com.honeypot.domain.reaction.mapper.ReactionMapper;
import com.honeypot.domain.reaction.repository.PostReactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class PostReactionServiceTest {

    private final ReactionMapper reactionMapper = Mappers.getMapper(ReactionMapper.class);

    @Mock
    private ReactionMapper reactionMapperMock;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostReactionRepository postReactionRepository;

    @Mock
    private MemberFindService memberFindService;

    @Mock
    private NotificationSendService notificationSendService;

    private PostReactionService postReactionService;

    @BeforeEach
    public void before() {
        postReactionService = new PostReactionService(
                reactionMapperMock,
                postRepository,
                postReactionRepository,
                memberFindService,
                notificationSendService
        );
    }

    @Test
    @DisplayName("게시글 작성자와 리액터가 같으며, 푸시 알림 미전송 확인")
    void save_PostWriterEqualsToReactor_NewReaction() {
        // Arrange
        Member reactor = Member.builder().id(999L).nickname("ReactorAndPostWriter").build();
        Post targetPost = createPost(1231L, reactor);

        ReactionRequest request = createPostLikeReactionRequest(reactor.getId(), targetPost.getId());
        PostReaction created = createPostReactionFromReactionRequest(1213L, request);

        ReactionDto expected = mockingPostReactionNotExists(request, reactor, targetPost, created);

        // Act
        ReactionDto result = postReactionService.save(request);

        // Assert
        assertEquals(expected, result);
        verifyNotificationSend(targetPost, result, never());
    }

    @Test
    @DisplayName("게시글 작성자와 리액터가 다르며 기존 리액션이 있을 경우, 푸시 알림 미전송 확인")
    void save_PostWriterIsNotEqualsToReactor_ReactionAlreadyExists() {
        // Arrange
        Member reactor = Member.builder().id(999L).nickname("reactor").build();
        Member postWriter = Member.builder().id(92L).nickname("postWriter").build();
        Post targetPost = createPost(142L, postWriter);

        ReactionRequest request = createPostLikeReactionRequest(reactor.getId(), targetPost.getId());
        PostReaction existed = createPostReactionFromReactionRequest(5555L, request);

        when(postRepository.findById(request.getTargetId())).thenReturn(Optional.of(targetPost));
        when(postReactionRepository.findByReactorIdAndPostId(request.getReactorId(), targetPost.getId()))
                .thenReturn(Optional.of(existed));

        when(memberFindService.findById(existed.getReactor().getId())).thenReturn(Optional.of(reactor));

        ReactionDto expected = reactionMapper.toDto(existed);
        when(reactionMapperMock.toDto(existed)).thenReturn(expected);
        expected.getReactor().setNickname(reactor.getNickname());

        // Act
        ReactionDto result = postReactionService.save(request);

        // Assert
        assertEquals(expected, result);
        verifyNotificationSend(targetPost, result, never());
    }

    @Test
    @DisplayName("게시글 작성자와 리액터가 다르며 기존 리액션이 없을 경우, 푸시 알림 전송 확인")
    void save_PostWriterIsNotEqualsToReactor_NewReaction() {
        // Arrange
        Member reactor = Member.builder().id(999L).nickname("reactor").build();
        Member postWriter = Member.builder().id(92L).nickname("postWriter").build();
        Post targetPost = createPost(142L, postWriter);

        ReactionRequest request = createPostLikeReactionRequest(reactor.getId(), targetPost.getId());
        PostReaction created = createPostReactionFromReactionRequest(5555L, request);
        ReactionDto expected = mockingPostReactionNotExists(request, reactor, targetPost, created);

        // Act
        ReactionDto result = postReactionService.save(request);

        // Assert
        assertEquals(expected, result);
        verifyNotificationSend(targetPost, result, times(1));
    }

    private ReactionRequest createPostLikeReactionRequest(Long reactorId, Long postId) {
        return ReactionRequest.builder()
                .reactorId(reactorId)
                .reactionType(ReactionType.LIKE)
                .targetId(postId)
                .targetType(String.valueOf(ReactionTarget.POST))
                .build();
    }

    private Post createPost(Long id, Member writer) {
        return Post.builder()
                .id(id)
                .writer(writer)
                .build();
    }

    private PostReaction createPostReactionFromReactionRequest(Long id, ReactionRequest request) {
        return PostReaction.builder()
                .id(id)
                .reactor(Member.builder().id(request.getReactorId()).build())
                .reactionType(request.getReactionType())
                .targetType(request.getTargetType())
                .postId(request.getTargetId())
                .build();
    }

    private ReactionDto mockingPostReactionNotExists(ReactionRequest request,
                                                     Member reactor,
                                                     Post targetPost,
                                                     PostReaction created
    ) {
        when(postRepository.findById(request.getTargetId())).thenReturn(Optional.of(targetPost));
        when(postReactionRepository.findByReactorIdAndPostId(request.getReactorId(), targetPost.getId()))
                .thenReturn(Optional.empty());

        PostReaction entity = reactionMapper.toPostReactionEntity(request);
        when(reactionMapperMock.toPostReactionEntity(request)).thenReturn(entity);
        when(postReactionRepository.save(entity)).thenReturn(created);
        when(memberFindService.findById(created.getReactor().getId())).thenReturn(Optional.of(reactor));

        ReactionDto expected = reactionMapper.toDto(created);
        when(reactionMapperMock.toDto(created)).thenReturn(expected);
        expected.getReactor().setNickname(reactor.getNickname());

        return expected;
    }

    private void verifyNotificationSend(Post targetPost,
                                        ReactionDto createdReaction,
                                        VerificationMode verificationMode
    ) {
        ReactionNotificationResource resource = ReactionNotificationResource.builder()
                .postResource(PostNotificationResource.builder()
                        .id(targetPost.getId())
                        .type(targetPost.getType())
                        .writer(targetPost.getWriter().getNickname())
                        .build())
                .reactionType(createdReaction.getReactionType())
                .reactor(createdReaction.getReactor().getNickname())
                .build();

        verify(notificationSendService, verificationMode).send(
                targetPost.getWriter().getId(),
                NotificationData.<ReactionNotificationResource>builder()
                        .type(NotificationType.LIKE_REACTION_TO_MY_POST)
                        .resource(resource)
                        .build()
        );
    }

}