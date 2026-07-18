package com.solux.web.domain.patient.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 환자 계정 매핑 엔티티. ERD 의 `환자` 테이블에 대응한다.
 * p_code(환자 코드)를 발급하고 회원(user_email)과 연결한다.
 * 코드는 1001 부터 발급된다.
 */
@Entity
@Getter
@Table(name = "patient")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "patient_code_gen")
    @TableGenerator(
            name = "patient_code_gen",
            table = "p_code_seq",
            pkColumnName = "seq_name",
            valueColumnName = "seq_value",
            pkColumnValue = "p_code",
            initialValue = 1000,
            allocationSize = 1
    )
    @Column(name = "p_code")
    private Integer pCode;

    @Column(name = "user_email", nullable = false, length = 100)
    private String userEmail;

    @Builder
    public Patient(String userEmail) {
        this.userEmail = userEmail;
    }
}
