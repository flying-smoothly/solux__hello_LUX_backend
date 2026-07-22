package com.solux.web.domain.doctor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 의사 진료 리포트. ERD 의 `의사_코멘트` 개념에 대응한다.
 * (의사 이메일, 환자 p_code) 당 하나의 리포트를 유지하며 작성/수정된다.
 */
@Entity
@Getter
@Table(name = "doctor_report",
        uniqueConstraints = @UniqueConstraint(columnNames = {"p_code", "user_email"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DoctorReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @Column(name = "p_code", nullable = false)
    private Integer pCode;

    @Column(name = "user_email", nullable = false, length = 100)
    private String userEmail;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "comment_date", nullable = false)
    private LocalDate commentDate;

    @Builder
    public DoctorReport(Integer pCode, String userEmail, String content) {
        this.pCode = pCode;
        this.userEmail = userEmail;
        this.content = content;
        this.commentDate = LocalDate.now();
    }

    public void updateContent(String content) {
        this.content = content;
        this.commentDate = LocalDate.now();
    }
}
