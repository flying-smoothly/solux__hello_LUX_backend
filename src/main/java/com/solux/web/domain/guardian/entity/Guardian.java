package com.solux.web.domain.guardian.entity;

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

/**
 * 보호자-환자 연동 엔티티. ERD 의 `보호자` 테이블에 대응한다.
 * 한 보호자가 여러 환자를, 한 환자가 여러 보호자를 가질 수 있어
 * (p_code, user_email) 조합을 유일하게 관리한다.
 */
@Entity
@Getter
@Table(name = "guardian",
        uniqueConstraints = @UniqueConstraint(columnNames = {"p_code", "user_email"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Guardian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guardian_id")
    private Long id;

    @Column(name = "p_code", nullable = false)
    private Integer pCode;

    @Column(name = "user_email", nullable = false, length = 100)
    private String userEmail;

    @Builder
    public Guardian(Integer pCode, String userEmail) {
        this.pCode = pCode;
        this.userEmail = userEmail;
    }
}
