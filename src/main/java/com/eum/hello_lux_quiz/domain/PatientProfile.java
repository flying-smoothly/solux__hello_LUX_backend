package com.eum.hello_lux_quiz.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "환자_프로필")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatientProfile {

    @Id
    @Column(name = "p_code", nullable = false)
    private Integer pCode;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "gender", nullable = false, length = 10)
    private String gender;

    @Column(name = "diagnosis", nullable = false, length = 255)
    private String diagnosis;

    @Column(name = "personality", nullable = false, columnDefinition = "TEXT")
    private String personality;

    @Column(name = "speech_style", nullable = false, columnDefinition = "TEXT")
    private String style;

    @Column(name = "patient_status")
    private String patientStatus = "유지"; // 기본값 '유지' (유지 / 주의 / 위험)

    public void updatePatientStatus(String status) {
        this.patientStatus = status;
    }
}
