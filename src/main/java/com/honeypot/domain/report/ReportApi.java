package com.honeypot.domain.report;

import com.honeypot.common.model.exceptions.InvalidTokenException;
import com.honeypot.common.utils.SecurityUtils;
import com.honeypot.domain.post.dto.GroupBuyingPostDto;
import com.honeypot.domain.post.dto.GroupBuyingPostUploadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Validated
public class ReportApi {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<?> upload(@Valid @RequestBody ReportUploadRequest uploadRequest) {
        Long memberId = SecurityUtils.getCurrentMemberId().orElseThrow(InvalidTokenException::new);
        uploadRequest.setReporterId(memberId);

        ReportDto uploaded = reportService.upload(uploadRequest);

        return ResponseEntity
                .created(ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{reportId}")
                        .buildAndExpand(uploaded.getReportId())
                        .toUri())
                .body(uploaded);
    }

}
