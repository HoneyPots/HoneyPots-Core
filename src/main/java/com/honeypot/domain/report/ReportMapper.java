package com.honeypot.domain.report;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", imports = ReportTarget.class)
public interface ReportMapper {

    @Mapping(target = "id", source = "reportId")
    @Mapping(target = "reporter.id", source = "reporterId")
    Report toEntity(ReportDto dto);

    @Mapping(target = "reporter.id", source = "reporterId")
    Report toEntity(ReportUploadRequest dto);

    @InheritInverseConfiguration
    ReportDto toDto(Report entity);

    List<ReportDto> toDto(List<Report> entities);

}
