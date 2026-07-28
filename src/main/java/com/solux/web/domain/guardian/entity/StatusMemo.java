package com.solux.web.domain.guardian.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 환자 상태 메모. ERD 의 `상태_메모` 테이블에 대응한다.
 * 보호자가 환자의 상태를 기록한다.
 */
@Entity
@Getter
@Table(name = "status_memo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StatusMemo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memo_id")
    private Long memoId;

    @Column(name = "p_code", nullable = false)
    private Integer pCode;

    @Column(name = "user_email", nullable = false, length = 100)
    private String userEmail;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

    @Builder
    public StatusMemo(Integer pCode, String userEmail, String content) {
        this.pCode = pCode;
        this.userEmail = userEmail;
        this.content = content;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }
}
