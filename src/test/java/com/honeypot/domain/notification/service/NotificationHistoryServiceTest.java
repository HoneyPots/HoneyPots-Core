package com.honeypot.domain.notification.service;

import com.honeypot.common.model.exceptions.InvalidAuthorizationException;
import com.honeypot.common.model.exceptions.InvalidTokenException;
import com.honeypot.common.utils.SecurityUtils;
import com.honeypot.domain.comment.entity.Comment;
import com.honeypot.domain.comment.repository.CommentRepository;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.service.MemberFindService;
import com.honeypot.domain.notification.dto.CommentNotificationResource;
import com.honeypot.domain.notification.dto.NotificationDto;
import com.honeypot.domain.notification.dto.ReactionNotificationResource;
import com.honeypot.domain.notification.entity.Notification;
import com.honeypot.domain.notification.entity.enums.NotificationType;
import com.honeypot.domain.notification.mapper.NotificationMapper;
import com.honeypot.domain.notification.repository.NotificationRepository;
import com.honeypot.domain.post.entity.Post;
import com.honeypot.domain.post.entity.enums.PostType;
import com.honeypot.domain.reaction.entity.PostReaction;
import com.honeypot.domain.reaction.entity.enums.ReactionTarget;
import com.honeypot.domain.reaction.entity.enums.ReactionType;
import com.honeypot.domain.reaction.repository.PostReactionRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class NotificationHistoryServiceTest {

    private final NotificationMapper notificationMapper = Mappers.getMapper(NotificationMapper.class);

    @Mock
    private MemberFindService memberFindService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostReactionRepository postReactionRepository;

    @InjectMocks
    private NotificationHistoryService notificationHistoryService;

    private static final List<Member> members = new ArrayList<>();

    private static final List<Notification> dummy = new ArrayList<>();

    private static MockedStatic<SecurityUtils> securityUtilsMockedStatic;

    static {
        for (long i = 1; i <= 3; i++) {
            members.add(Member.builder().id(i).nickname("nickname" + i).build());
        }

        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 100; i++) {
            int randomNum = ThreadLocalRandom.current().nextInt(0, members.size());
            Member member = members.get(randomNum);

            Notification notification = Notification.builder()
                    .id(i + 1L)
                    .member(member)
                    .titleMessage(String.format("this is test message (%d)", i + 1L))
                    .contentMessage(String.format("this is test message (%d)", i + 1L))
                    .type(i < 50 ? NotificationType.COMMENT_TO_POST : NotificationType.LIKE_REACTION_TO_POST)
                    .referenceId(i + 1L)
                    .createdAt(now)
                    .lastModifiedAt(now)
                    .build();
            dummy.add(notification);
        }
    }

    @BeforeAll
    public static void setup() {
        securityUtilsMockedStatic = mockStatic(SecurityUtils.class);
    }

    @AfterAll
    public static void teardown() {
        securityUtilsMockedStatic.close();
    }


    @BeforeEach
    public void before() {
        this.notificationHistoryService = new NotificationHistoryService(
                memberFindService,
                notificationMapper,
                notificationRepository,
                commentRepository,
                postReactionRepository
        );
    }

    @Test
    void findNotificationResourceById_AuthenticationError() {
        // Arrange
        Long notificationId = 1L;
        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidTokenException.class, () -> {
            notificationHistoryService.findNotificationResourceById(notificationId);
        });
    }

    @Test
    void findNotificationResourceById_MemberNotFoundOrWithdrawal() {
        // Arrange
        Long notificationId = 1L;
        Member member = Member.builder().id(2L).build();
        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(member.getId()));
        when(memberFindService.findById(member.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            notificationHistoryService.findNotificationResourceById(notificationId);
        });
    }

    @Test
    void findNotificationResourceById_NotificationNotFound() {
        // Arrange
        Long notificationId = 1L;
        Member member = Member.builder().id(2L).build();
        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(member.getId()));
        when(memberFindService.findById(member.getId())).thenReturn(Optional.of(member));
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            notificationHistoryService.findNotificationResourceById(notificationId);
        });
    }

    @Test
    void findNotificationResourceById_Unauthorized() {
        // Arrange
        Long notificationId = 10L;
        Notification notification = findDummy(notificationId);
        Member member = notification.getMember();
        Member anotherMember = Member.builder().id(member.getId() + 1).build();
        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(anotherMember.getId()));
        when(memberFindService.findById(anotherMember.getId())).thenReturn(Optional.of(anotherMember));
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        // Act & Assert
        assertThrows(InvalidAuthorizationException.class, () -> {
            notificationHistoryService.findNotificationResourceById(notificationId);
        });
    }

    @Test
    void findNotificationResourceById_CommentResource() {
        // Arrange
        Long notificationId = 10L;
        Notification notification = findDummy(notificationId);
        Member member = notification.getMember();
        Long memberId = member.getId();
        Long referenceId = notification.getReferenceId();
        Post post = Post.builder()
                .id(2L)
                .type(PostType.NORMAL)
                .title("this is test post title")
                .writer(Member.builder().id(123L).nickname("postWriter").build())
                .build();
        Comment commentResource = Comment.builder()
                .id(referenceId)
                .content("this is test comment content")
                .post(post)
                .writer(Member.builder().id(22L).nickname("commentWriter").build())
                .build();

        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(memberId));
        when(memberFindService.findById(memberId)).thenReturn(Optional.of(member));
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(commentRepository.findById(referenceId)).thenReturn(Optional.of(commentResource));

        // Act
        CommentNotificationResource result
                = (CommentNotificationResource) notificationHistoryService.findNotificationResourceById(notificationId);

        // Assert
        assertEquals(result.getCommentId(), commentResource.getId());
        assertEquals(result.getCommenter(), commentResource.getWriter().getNickname());
        assertEquals(result.getPostResource().getId(), post.getId());
        assertEquals(result.getPostResource().getType(), post.getType());
        assertEquals(result.getPostResource().getWriter(), post.getWriter().getNickname());
        assertEquals(result.getReferenceId(), notification.getReferenceId());
    }

    @Test
    void findNotificationResourceById_ReactionResource() {
        // Arrange
        Long notificationId = 60L;
        Notification notification = findDummy(notificationId);
        Member member = notification.getMember();
        Long memberId = member.getId();
        Long referenceId = notification.getReferenceId();
        Post post = Post.builder()
                .id(2L)
                .type(PostType.GROUP_BUYING)
                .title("this is test post title")
                .writer(Member.builder().id(123L).nickname("postWriter").build())
                .build();
        PostReaction reaction = PostReaction.builder()
                .id(referenceId)
                .post(post)
                .reactor(Member.builder().id(22L).nickname("postLikeReactor").build())
                .reactionType(ReactionType.LIKE)
                .targetType(ReactionTarget.POST)
                .build();

        when(SecurityUtils.getCurrentMemberId()).thenReturn(Optional.of(memberId));
        when(memberFindService.findById(memberId)).thenReturn(Optional.of(member));
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(postReactionRepository.findById(referenceId)).thenReturn(Optional.of(reaction));

        // Act
        ReactionNotificationResource result
                = (ReactionNotificationResource) notificationHistoryService.findNotificationResourceById(notificationId);

        // Assert
        assertEquals(result.getReactionId(), reaction.getId());
        assertEquals(result.getReactor(), reaction.getReactor().getNickname());
        assertEquals(result.getReactionType(), reaction.getReactionType());
        assertEquals(result.getPostResource().getId(), post.getId());
        assertEquals(result.getPostResource().getType(), post.getType());
        assertEquals(result.getPostResource().getWriter(), post.getWriter().getNickname());
        assertEquals(result.getReferenceId(), notification.getReferenceId());
    }

    @Test
    void findByMemberWithPagination() {
        // Arrange
        Long memberId = 1L;
        Member member = members.get(0);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createdAt")));

        Page<Notification> page = createNotificationPage(pageable, member);
        when(memberFindService.findById(memberId)).thenReturn(Optional.of(member));
        when(notificationRepository.findByMember(member, pageable)).thenReturn(page);

        // Act
        Page<NotificationDto> result = notificationHistoryService.findByMemberWithPagination(memberId, pageable);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().size() <= page.getSize());
        for (NotificationDto dto : result.getContent()) {
            Notification compare = findDummy(dto.getNotificationId());
            assertEquals(compare.getId(), dto.getNotificationId());
            assertEquals(compare.getType(), dto.getType());
            assertEquals(compare.getTitleMessage(), dto.getTitleMessage());
            assertEquals(compare.getContentMessage(), dto.getContentMessage());
            assertEquals(compare.getCreatedAt(), dto.getCreatedAt());
            assertEquals(compare.getLastModifiedAt(), dto.getLastModifiedAt());
        }
    }

    private Page<Notification> createNotificationPage(Pageable pageable, Member member) {
        List<Notification> list = dummy.stream()
                .filter(n -> n.getMember().equals(member))
                .toList();

        int start = (int) pageable.getOffset();
        int end = (int) (pageable.getOffset() + pageable.getPageSize());
        end = Math.min(end, list.size());

        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }


    private Notification findDummy(Long notificationId) {
        return dummy.stream().filter(n -> n.getId().equals(notificationId)).findFirst().orElse(null);
    }

}