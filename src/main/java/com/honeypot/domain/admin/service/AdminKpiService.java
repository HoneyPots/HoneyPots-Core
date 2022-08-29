package com.honeypot.domain.admin.service;

import com.honeypot.domain.admin.dto.ActiveUsersKpiResponse;
import com.honeypot.domain.admin.repository.AdminKpiRepository;
import com.honeypot.domain.admin.dto.JoinMemberKpiResponse;
import com.honeypot.domain.admin.dto.UserEngagementKpiResponse;
import com.honeypot.domain.admin.repository.LogItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminKpiService {

    private final AdminKpiRepository adminKpiRepository;

    private final LogItemRepository logItemRepository;

    @Transactional(readOnly = true)
    public List<JoinMemberKpiResponse> getJoinMemberCountKpi(LocalDate fromDate, LocalDate toDate) {
        return adminKpiRepository.getJoinMemberCountKpi(fromDate, toDate);
    }

    @Transactional(readOnly = true)
    public List<ActiveUsersKpiResponse> getActiveUsersKpi(LocalDate fromDate, LocalDate toDate) {
        return logItemRepository.getActiveUsersKpi(fromDate, toDate);
    }

    @Transactional(readOnly = true)
    public UserEngagementKpiResponse getUserEngagementKpi(LocalDate fromDate, LocalDate toDate) {
        return adminKpiRepository.getUserEngagementKpi(fromDate, toDate);
    }

}
