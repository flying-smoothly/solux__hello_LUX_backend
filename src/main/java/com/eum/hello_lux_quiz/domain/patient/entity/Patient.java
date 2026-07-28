package com.eum.hello_lux_quiz.domain.patient.entity;

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
 * 환자 엔티티. ERD 의 `환자`(계정 매핑) + `환자_프로필`(상세)을 하나로 병합한 테이블이다.
 * p_code(환자 코드)를 발급하고 회원(user_email)과 연결하며, 환자 상세 정보를 함께 보관한다.
 * 코드는 1001 부터 발급된다.
 *
 * 코드는 1001 부터 발급된다.
 * <p>이름/생년월일은 공통 정보이므로 {@code Member} 에서만 관리한다(중복 제거).</p>
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

    @Column(name = "gender", nullable = false, length = 10)
    private String gender;

    @Column(name = "diagnosis", nullable = false, length = 100)
    private String diagnosis;

    @Column(name = "personality", nullable = false, columnDefinition = "TEXT")
    private String personality;

    @Column(name = "speech_style", nullable = false, columnDefinition = "TEXT")
    private String speechStyle;

    @Column(name = "patient_status", length = 50)
    private String patientStatus;

    @Builder
    public Patient(String userEmail, String gender, String diagnosis,
                   String personality, String speechStyle, String patientStatus) {
        this.userEmail = userEmail;
        this.gender = gender;
        this.diagnosis = diagnosis;
        this.personality = personality;
        this.speechStyle = speechStyle;
        this.patientStatus = patientStatus;
    }

    public void update(String personality, String speechStyle) {
        if (personality != null && !personality.isBlank()) {
            this.personality = personality;
        }
        if (speechStyle != null && !speechStyle.isBlank()) {
            this.speechStyle = speechStyle;
        }
    }
}
