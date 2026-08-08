package com.eum.hello_lux_quiz.domain.guardian.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 보호자-환자 연동 응답.
 * 예: {"message":"연동 완료","patient_name":"홍길동"}
 */
public record LinkResponse(
        String message,
        @JsonProperty("patient_name") String patientName
) {
}
