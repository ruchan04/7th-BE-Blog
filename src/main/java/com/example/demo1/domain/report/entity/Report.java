package com.example.demo1.domain.report.entity;

import com.example.demo1.domain.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
@AllArgsConstructor
public class Report extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reporterId; // 신고자 ID
    private Long targetId;   // 게시물 또는 댓글 ID

    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.PENDING; // 기본값: 대기중

    public void resolve() {
        this.status = ReportStatus.RESOLVED;
    }


}