package com.honeypot.domain.notification.service;

import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.member.service.MemberFindService;
import com.honeypot.domain.notification.dto.NotificationDto;
import com.honeypot.domain.notification.entity.Notification;
import com.honeypot.domain.notification.entity.enums.NotificationType;
import com.honeypot.domain.notification.mapper.NotificationMapper;
import com.honeypot.domain.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class NotificationHistoryServiceTest {

    private final NotificationMapper notificationMapper = Mappers.getMapper(NotificationMapper.class);

    @Mock
    private MemberFindService memberFindService;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationHistoryService notificationHistoryService;

    private static final List<Member> members = new ArrayList<>();

    private static final List<Notification> dummy = new ArrayList<>();

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
                    .message(String.format("this is test message (%d)", i + 1L))
                    .type(NotificationType.COMMENT_TO_MY_POST)
                    .createdAt(now)
                    .lastModifiedAt(now)
                    .build();
            dummy.add(notification);
        }
    }

    @BeforeEach
    public void before() {
        this.notificationHistoryService = new NotificationHistoryService(
                memberFindService,
                notificationMapper,
                notificationRepository
        );
    }

    @Test
    void findByMemberWithPagination() {
        // Arrange
        Long memberId = 1L;
        Member member = members.get(0);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createdAt")));

        Page<Notification> page = createNotificationPage(pageable, member);
        when(memberFindService.findById(memberId)).thenReturn(Optional.of(member));
        when(notificationRepository.findByMemberWithPagination(member, pageable)).thenReturn(page);

        // Act
        Page<NotificationDto> result = notificationHistoryService.findByMemberWithPagination(memberId, pageable);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().size() <= page.getSize());
        for (NotificationDto dto : result.getContent()) {
            Notification compare = findDummy(dto.getNotificationId());
            assertEquals(compare.getId(), dto.getNotificationId());
            assertEquals(compare.getType(), dto.getType());
            assertEquals(compare.getMessage(), dto.getMessage());
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