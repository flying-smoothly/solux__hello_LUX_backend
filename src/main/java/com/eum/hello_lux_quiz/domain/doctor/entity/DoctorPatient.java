package com.eum.hello_lux_quiz.domain.doctor.entity;

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
 * 의사-환자 연동 엔티티.
 * 한 의사가 여러 환자를 담당할 수 있어 (p_code, user_email) 조합을 유일하게 관리한다.
 */
@Entity
@Getter
@Table(name = "doctor_patient",
        uniqueConstraints = @UniqueConstraint(columnNames = {"p_code", "user_email"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DoctorPatient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_patient_id")
    private Long id;

    @Column(name = "p_code", nullable = false)
    private Integer pCode;

    @Column(name = "user_email", nullable = false, length = 100)
    private String userEmail;

    @Builder
    public DoctorPatient(Integer pCode, String userEmail) {
        this.pCode = pCode;
        this.userEmail = userEmail;
    }
}
