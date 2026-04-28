package com.example.demo1.domain.report.repository;

import com.example.demo1.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    // [중복 방지] 신고자와 타겟 ID로 이미 데이터가 있는지 확인하는 메서드
    boolean existsByReporterIdAndTargetId(Long reporterId, Long targetId);
}