package com.example.demo1.domain.report.service;

import com.example.demo1.domain.report.entity.Report;
import com.example.demo1.domain.report.entity.ReportStatus;
import com.example.demo1.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;

    @Transactional
    public String createReport(Long reporterId, Long targetId) {
        // [오류 해결 포인트 1] 리포지토리에 existsByReporterIdAndTargetId 메서드가 선언되어 있어야 합니다.
        if (reportRepository.existsByReporterIdAndTargetId(reporterId, targetId)) {
            throw new IllegalStateException("이미 신고한 대상입니다.");
        }

        Report report = Report.builder()
                .reporterId(reporterId)
                .targetId(targetId)
                .status(ReportStatus.PENDING)
                .build();

        reportRepository.save(report);
        return "신고가 정상적으로 접수되었습니다.";
    }


}