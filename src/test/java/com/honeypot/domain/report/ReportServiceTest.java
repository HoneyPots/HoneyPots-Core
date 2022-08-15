package com.honeypot.domain.report;

import com.honeypot.common.model.exceptions.ReportTargetNotFoundException;
import com.honeypot.domain.comment.repository.CommentRepository;
import com.honeypot.domain.member.entity.Member;
import com.honeypot.domain.post.entity.Post;
import com.honeypot.domain.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ReportMapper reportMapper;

    private ReportService reportService;

    @BeforeEach
    private void before() {
        this.reportService = new ReportService(reportRepository, postRepository, commentRepository, reportMapper);
    }

    @Test
    void upload() {
        // Arrange
        ReportTarget target = ReportTarget.POST;
        Long targetId = 1L;
        String reason = "사유";
        Long reporterId = 2L;

        ReportUploadRequest uploadRequest = ReportUploadRequest.builder()
                .target(target)
                .targetId(targetId)
                .reason(reason)
                .reporterId(reporterId)
                .build();

        Report toEntity = Report.builder()
                .target(target)
                .targetId(targetId)
                .reason(reason)
                .reporter(Member.builder().id(reporterId).build())
                .build();

        LocalDateTime createdAt = LocalDateTime.now();
        Report uploaded = Report.builder()
                .id(1L)
                .target(target)
                .targetId(targetId)
                .reason(reason)
                .reporter(Member.builder().id(reporterId).build())
                .createdAt(createdAt)
                .lastModifiedAt(createdAt)
                .build();

        ReportDto expected = ReportDto.builder()
                .reportId(1L)
                .target(target)
                .targetId(targetId)
                .reason(reason)
                .reporterId(reporterId)
                .createdAt(createdAt)
                .lastModifiedAt(createdAt)
                .build();

        when(postRepository.findById(targetId)).thenReturn(Optional.of(Post.builder().build()));
        when(reportMapper.toEntity(uploadRequest)).thenReturn(toEntity);
        when(reportRepository.save(toEntity)).thenReturn(uploaded);
        when(reportMapper.toDto(uploaded)).thenReturn(expected);

        // Act
        ReportDto result = reportService.upload(uploadRequest);

        // Assert
        verify(commentRepository, never()).findById(targetId);
        verify(postRepository, times(1)).findById(targetId);
        verify(reportRepository, times(1)).save(toEntity);
        assertEquals(expected, result);
    }

    @Test
    void upload_ReportTargetNotFoundException() {
        // Arrange
        ReportTarget target = ReportTarget.POST;
        Long targetId = 1L;
        String reason = "사유";
        Long reporterId = 2L;

        ReportUploadRequest uploadRequest = ReportUploadRequest.builder()
                .target(target)
                .targetId(targetId)
                .reason(reason)
                .reporterId(reporterId)
                .build();

        when(postRepository.findById(targetId)).thenThrow(new ReportTargetNotFoundException(target, targetId));

        // Act & Assert
        assertThrows(ReportTargetNotFoundException.class, () -> {
            reportService.upload(uploadRequest);
        });
    }

}