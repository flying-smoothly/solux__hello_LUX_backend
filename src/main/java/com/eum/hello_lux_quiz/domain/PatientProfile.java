package com.eum.hello_lux_quiz.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "patient_profile")
@Getter
@Setter
@NoArgsConstructor
public class PatientProfile {

    @Id
    @Column(name = "p_code")
    private Integer pCode; // 환자 식별 코드 (PK)[cite: 2]

    //  누락되어 있던 name, style 필드 추가
    @Column(name = "name")
    private String name;   // 환자 이름

    @Column(name = "style")
    private String style;  // 스타일 설정

    @Column(name = "patient_status")
    private String patientStatus; // 환자 상태 (유지/경도 등)[cite: 2]

    // --- 신규 화면 입력 필드 ---
    @Column(name = "cognitive_level")
    private String cognitiveLevel = "보통"; // 인지 지원 수준 (낮음 / 보통 / 높음)[cite: 2]

    @Column(name = "is_guardian_present")
    private Boolean isGuardianPresent = false; // 보호자 동행 여부[cite: 2]

    // --- 음성 설정 통합 (JSON) ---
    @Convert(converter = VoiceSettingConverter.class)
    @Column(name = "voice_setting", columnDefinition = "json")
    private VoiceSetting voiceSetting = new VoiceSetting();

    @Column(name = "personality")
    private String personality; // 성격/특성 (화면 입력칸은 없지만 유지)[cite: 2]

    public void updatePatientStatus(String patientStatus) {
        this.patientStatus = patientStatus;
    }
}

