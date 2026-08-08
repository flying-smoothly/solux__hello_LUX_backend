package com.eum.hello_lux_quiz.domain.guardian.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;

/**
 * 보호자-환자 연동 요청. 환자가 발급받은 6자리 연동 코드(p_code)를 입력한다.
 */
public record LinkRequest(
        @JsonProperty("p_code")
        @NotBlank(message = "환자 코드(p_code)는 필수입니다.")
        String patientCode
) {
}
