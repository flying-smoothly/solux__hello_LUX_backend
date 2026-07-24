package com.eum.hello_lux_quiz.dto;

public class PatientStatusUpdateRequest {

    private String patientStatus;

    // 기본 수동 생성자 (Jackson deserialization용)
    public PatientStatusUpdateRequest() {
    }

    // 매개변수가 있는 수동 생성자
    public PatientStatusUpdateRequest(String patientStatus) {
        this.patientStatus = patientStatus;
    }

    // Getter & Setter 수동 작성
    public String getPatientStatus() {
        return patientStatus;
    }

    public void setPatientStatus(String patientStatus) {
        this.patientStatus = patientStatus;
    }
}
