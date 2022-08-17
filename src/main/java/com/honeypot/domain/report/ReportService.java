package com.honeypot.domain.report;

import com.honeypot.common.model.exceptions.ReportTargetNotFoundException;
import com.honeypot.common.validation.groups.InsertContext;
import com.honeypot.domain.comment.repository.CommentRepository;
import com.honeypot.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
@Validated
public class ReportService {

    private final ReportRepository reportRepository;

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    private final ReportMapper reportMapper;

    @Transactional
    @Validated(InsertContext.class)
    public ReportDto upload(@Valid ReportUploadRequest request) {
        ReportTarget target = request.getTarget();
        Long targetId = request.getTargetId();
        if (target == ReportTarget.POST) {
            postRepository.findById(targetId).orElseThrow(() -> new ReportTargetNotFoundException(target, targetId));
        } else if (target == ReportTarget.COMMENT) {
            commentRepository.findById(targetId).orElseThrow(() -> new ReportTargetNotFoundException(target, targetId));
        }

        Report uploaded = reportRepository.save(reportMapper.toEntity(request));
        return reportMapper.toDto(uploaded);
    }

}
