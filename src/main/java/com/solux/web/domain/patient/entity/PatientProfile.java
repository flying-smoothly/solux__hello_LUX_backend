package com.solux.web.domain.patient.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 환자 상세 프로필. ERD 의 `환자_프로필` 테이블에 대응한다.
 * Patient 와 동일한 p_code 를 기본키로 공유한다(1:1).
 * style 필드는 API 명세의 speech_style(말투) 에 대응한다.
 */
@Entity
@Getter
@Table(name = "patient_profile")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatientProfile {

    @Id
    @Column(name = "p_code")
    private Integer pCode;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "gender", nullable = false, length = 10)
    private String gender;

    @Column(name = "diagnosis", nullable = false, length = 100)
    private String diagnosis;

    @Column(name = "personality", nullable = false, columnDefinition = "TEXT")
    private String personality;

    @Column(name = "style", nullable = false, columnDefinition = "TEXT")
    private String style;

    @Builder
    public PatientProfile(Integer pCode, String name, LocalDate birthDate, String gender,
                          String diagnosis, String personality, String style) {
        this.pCode = pCode;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.diagnosis = diagnosis;
        this.personality = personality;
        this.style = style;
    }

    public void update(String personality, String style) {
        if (personality != null && !personality.isBlank()) {
            this.personality = personality;
        }
        if (style != null && !style.isBlank()) {
            this.style = style;
        }
    }
}
