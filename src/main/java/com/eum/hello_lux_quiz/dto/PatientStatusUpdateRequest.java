package com.eum.hello_lux_quiz.dto;

public class PatientStatusUpdateRequest {

    private String patientStatus;
    private String cognitiveLevel;      // 추가: 인지 지원 수준 ("낮음", "보통", "높음")
    private Boolean isGuardianPresent;  // 추가: 보호자 동행 여부 (true / false)

    // 기본 수동 생성자 (Jackson deserialization용)
    public PatientStatusUpdateRequest() {
    }

    // 모든 필드를 받는 수동 생성자
    public PatientStatusUpdateRequest(String patientStatus, String cognitiveLevel, Boolean isGuardianPresent) {
        this.patientStatus = patientStatus;
        this.cognitiveLevel = cognitiveLevel;
        this.isGuardianPresent = isGuardianPresent;
    }

    // --- Getter & Setter ---
    public String getPatientStatus() {
        return patientStatus;
    }

    public void setPatientStatus(String patientStatus) {
        this.patientStatus = patientStatus;
    }

    public String getCognitiveLevel() {
        return cognitiveLevel;
    }

    public void setCognitiveLevel(String cognitiveLevel) {
        this.cognitiveLevel = cognitiveLevel;
    }

    public Boolean getIsGuardianPresent() {
        return isGuardianPresent;
    }

    public void setIsGuardianPresent(Boolean isGuardianPresent) {
        this.isGuardianPresent = isGuardianPresent;
    }
}
