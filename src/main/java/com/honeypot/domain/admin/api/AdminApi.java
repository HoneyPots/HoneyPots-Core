package com.honeypot.domain.admin.api;

import com.honeypot.domain.admin.service.AdminKpiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/admin/")
@RequiredArgsConstructor
public class AdminApi {

    private final AdminKpiService adminKpiService;

    @GetMapping("/kpi")
    public ResponseEntity<?> getKpi(@RequestParam String type,
                                    @RequestParam String from,
                                    @RequestParam String to) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate fromDate = LocalDate.parse(from, formatter);
        LocalDate toDate = LocalDate.parse(to, formatter);

        if ("joinMemberCount".equals(type)) {
            return ResponseEntity.ok(adminKpiService.getJoinMemberCountKpi(fromDate, toDate));
        } else if ("activeUserCount".equals(type)) {
            return ResponseEntity.ok(adminKpiService.getActiveUsersKpi(fromDate, toDate));
        } else if ("userEngagement".equals(type)) {
            return ResponseEntity.ok(adminKpiService.getUserEngagementKpi(fromDate, toDate));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}