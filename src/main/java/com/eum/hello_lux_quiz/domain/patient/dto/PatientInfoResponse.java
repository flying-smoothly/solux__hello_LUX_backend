package com.eum.hello_lux_quiz.domain.patient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.eum.hello_lux_quiz.domain.patient.entity.Patient;

/**
 * 환자 정보 조회 응답.
 * 예: {"p_code":"AB37X2","name":"홍길동","diagnosis":"경도인지장애","personality":"온화함"}
 * 이름은 회원(Member) 에서 관리하므로 별도로 전달받는다.
 */
public record PatientInfoResponse(
        @JsonProperty("p_code") String pCode,
        String name,
        String diagnosis,
        String personality
) {
    public static PatientInfoResponse from(Patient patient, String name) {
        return new PatientInfoResponse(
                patient.getPatientCode(),
                name,
                patient.getDiagnosis(),
                patient.getPersonality()
        );
    }
}
