package com.eum.hello_lux_quiz.domain.patient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

import com.eum.hello_lux_quiz.domain.patient.entity.Patient;

/**
 * 환자 정보 조회 응답.
 * 예: {"p_code":1001,"name":"홍길동","birth_date":"1950-01-01","diagnosis":"경도인지장애","personality":"온화함"}
 * 이름/생년월일은 회원(Member) 에서 관리하므로 별도로 전달받는다.
 */
public record PatientInfoResponse(
        @JsonProperty("p_code") Integer pCode,
        String name,
        @JsonProperty("birth_date") LocalDate birthDate,
        String diagnosis,
        String personality
) {
    public static PatientInfoResponse from(Patient patient, String name, LocalDate birthDate) {
        return new PatientInfoResponse(
                patient.getPCode(),
                name,
                birthDate,
                patient.getDiagnosis(),
                patient.getPersonality()
        );
    }
}
