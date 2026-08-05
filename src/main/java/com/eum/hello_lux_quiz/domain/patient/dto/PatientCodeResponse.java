package com.eum.hello_lux_quiz.domain.patient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 환자 코드 조회 응답. 보호자·의사 연동에 사용하는 6자리 코드다.
 * 예: {"p_code":"AB37X2"}
 */
public record PatientCodeResponse(
        @JsonProperty("p_code") String pCode
) {
}
