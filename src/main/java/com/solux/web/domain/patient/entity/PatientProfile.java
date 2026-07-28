package com.solux.web.domain.patient.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 환자 상세 프로필. ERD 의 `환자_프로필` 테이블에 대응한다.
 * Patient 와 동일한 p_code 를 기본키로 공유한다(1:1).
 *
 * <p>이름/생년월일은 공통 정보이므로 {@code Member} 에서만 관리한다(중복 제거).
 * speech_style 은 API 명세의 말투 필드에 대응한다.</p>
 */
@Entity
@Getter
@Table(name = "patient_profile")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatientProfile {

    @Id
    @Column(name = "p_code")
    private Integer pCode;

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
    public PatientProfile(Integer pCode, String gender, String diagnosis,
                          String personality, String speechStyle, String patientStatus) {
        this.pCode = pCode;
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
