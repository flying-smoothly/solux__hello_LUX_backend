package com.eum.hello_lux_quiz.domain.doctor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.eum.hello_lux_quiz.domain.patient.entity.Patient;

/**
 * 담당 환자 목록 항목.
 * 예: {"p_code":1001,"name":"홍길동","diagnosis":"경도인지장애"}
 * 이름은 회원(Member) 에서 관리하므로 별도로 전달받는다.
 */
public record DoctorPatientResponse(
        @JsonProperty("p_code") Integer pCode,
        String name,
        String diagnosis
) {
    public static DoctorPatientResponse from(Patient patient, String name) {
        return new DoctorPatientResponse(patient.getPCode(), name, patient.getDiagnosis());
    }
}
