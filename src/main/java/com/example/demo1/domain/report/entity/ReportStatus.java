package com.example.demo1.domain.report.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportStatus {
    PENDING("대기 중"),
    RESOLVED("처리 완료");

    private final String description;
}