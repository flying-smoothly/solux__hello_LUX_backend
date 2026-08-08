package com.eum.hello_lux_quiz.domain.patient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 환자 등록 응답. p_code 는 보호자·의사 연동용 6자리 코드다.
 * 예: {"p_code":"AB37X2","internal_code":1001,"message":"환자 등록 완료"}
 */
public record PatientRegisterResponse(
        @JsonProperty("internal_code") Integer internalCode,
        @JsonProperty("p_code") String pCode,
        String message
) {
}
