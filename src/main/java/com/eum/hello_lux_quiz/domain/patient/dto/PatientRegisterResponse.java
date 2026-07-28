package com.eum.hello_lux_quiz.domain.patient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 환자 등록 응답.
 * 예: {"p_code":1001,"message":"환자 등록 완료"}
 */
public record PatientRegisterResponse(
        @JsonProperty("p_code") Integer pCode,
        String message
) {
}
