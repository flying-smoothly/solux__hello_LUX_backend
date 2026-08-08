package com.eum.hello_lux_quiz.domain.doctor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * 의사-환자 연동 요청. 환자가 발급받은 6자리 연동 코드(p_code)를 입력한다.
 * 예: {"p_code":"AB37X2"}
 */
public record DoctorLinkRequest(
        @JsonProperty("p_code")
        @NotBlank(message = "환자 코드(p_code)는 필수입니다.")
        String patientCode
) {
}
