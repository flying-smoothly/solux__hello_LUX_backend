package com.solux.web.domain.doctor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

/**
 * 의사-환자 연동 요청.
 * 예: {"p_code":1001}
 */
public record DoctorLinkRequest(
        @JsonProperty("p_code")
        @NotNull(message = "환자 코드(p_code)는 필수입니다.")
        Integer pCode
) {
}
