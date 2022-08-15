package com.honeypot.domain.report;

import com.honeypot.domain.member.entity.Member;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportMapperTest {

    private final ReportMapper mapper = Mappers.getMapper(ReportMapper.class);

    @Test
    void toEntity() {
        // Arrange
        LocalDateTime createdAt = LocalDateTime.of(2022, 7, 28, 22, 0, 0);
        ReportDto dto = ReportDto.builder()
                .reportId(1L)
                .target(ReportTarget.POST)
                .targetId(2921L)
                .reason("그냥 신고하고 싶었습니다.")
                .reporterId(12L)
                .createdAt(createdAt)
                .lastModifiedAt(createdAt)
                .build();

        // Act
        Report entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getReportId(), entity.getId());
        assertEquals(dto.getTarget(), entity.getTarget());
        assertEquals(dto.getTargetId(), entity.getTargetId());
        assertEquals(dto.getReason(), entity.getReason());
        assertEquals(dto.getReporterId(), entity.getReporter().getId());
        assertEquals(dto.getCreatedAt(), entity.getCreatedAt());
        assertEquals(dto.getLastModifiedAt(), entity.getLastModifiedAt());
    }

    @Test
    void toEntity_FromReportUploadRequest() {
        // Arrange
        ReportUploadRequest dto = ReportUploadRequest.builder()
                .target(ReportTarget.POST)
                .targetId(123123L)
                .reason("내맘..")
                .reporterId(1L)
                .build();

        // Act
        Report entity = mapper.toEntity(dto);

        // Assert
        assertEquals(dto.getTarget(), entity.getTarget());
        assertEquals(dto.getTargetId(), entity.getTargetId());
        assertEquals(dto.getReason(), entity.getReason());
        assertEquals(dto.getReporterId(), entity.getReporter().getId());
    }

    @Test
    void toDto() {
        // Arrange
        LocalDateTime createdAt = LocalDateTime.of(2022, 7, 28, 22, 0, 0);
        Report entity = Report.builder()
                .id(1L)
                .target(ReportTarget.POST)
                .targetId(123L)
                .reason("신고사유")
                .reporter(Member.builder().id(1L).build())
                .createdAt(createdAt)
                .lastModifiedAt(createdAt)
                .build();

        // Act
        ReportDto dto = mapper.toDto(entity);

        // Assert
        assertEquals(entity.getId(), dto.getReportId());
        assertEquals(entity.getTarget(), dto.getTarget());
        assertEquals(entity.getTargetId(), dto.getTargetId());
        assertEquals(entity.getReason(), dto.getReason());
        assertEquals(entity.getReporter().getId(), dto.getReporterId());
        assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
        assertEquals(entity.getLastModifiedAt(), dto.getLastModifiedAt());
    }

}