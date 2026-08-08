package com.eum.hello_lux_quiz.domain.doctor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
* 의사-환자 연동 응답.
 * 예: {"message":"연동 완료","patient_name":"홍길동"}
 */
public record DoctorLinkResponse(
        String message,
        @JsonProperty("patient_name") String patientName
) {
}