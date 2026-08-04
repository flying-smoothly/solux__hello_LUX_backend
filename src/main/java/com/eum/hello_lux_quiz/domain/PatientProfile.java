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

    // ================= 개인화 설정 7가지  =================

    @Column(name = "tts_speed")
    private Double ttsSpeed = 1.0;          // 1. TTS 속도 (기본값: 1.0)

    @Column(name = "sentence_length")
    private String sentenceLength = "보통"; // 2. 문장 길이 (짧게 / 보통 / 길게)

    @Column(name = "is_honorific")
    private Boolean isHonorific = true;     // 3. 존댓말 여부 (기본값: true)

    @Column(name = "is_auto_play")
    private Boolean isAutoPlay = false;     // 4. 자동재생 여부 (기본값: false)

    @Column(name = "is_repeat_guide")
    private Boolean isRepeatGuide = true;   // 5. 반복안내 여부 (기본값: true)

    @Column(name = "is_low_pressure")
    private Boolean isLowPressure = true;   // 6. 압박감 낮춤 여부 (기본값: true)

    @Column(name = "is_positive_feedback")
    private Boolean isPositiveFeedback = true; // 7. 긍정 피드백 여부 (기본값: true)


    // 환자 상태 수정 메서드
    public void updatePatientStatus(String status) {
        this.patientStatus = status;
    }

    //  설정 정보 수정 메서드
    public void updateSettings(Double ttsSpeed, String sentenceLength, Boolean isHonorific,
                               Boolean isAutoPlay, Boolean isRepeatGuide, 
                               Boolean isLowPressure, Boolean isPositiveFeedback) {
        if (ttsSpeed != null) this.ttsSpeed = ttsSpeed;
        if (sentenceLength != null) this.sentenceLength = sentenceLength;
        if (isHonorific != null) this.isHonorific = isHonorific;
        if (isAutoPlay != null) this.isAutoPlay = isAutoPlay;
        if (isRepeatGuide != null) this.isRepeatGuide = isRepeatGuide;
        if (isLowPressure != null) this.isLowPressure = isLowPressure;
        if (isPositiveFeedback != null) this.positiveFeedback = isPositiveFeedback;
    }
}
