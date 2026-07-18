package com.solux.web.domain.patient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 환자 코드 조회 응답.
 * 예: {"p_code":1001}
 */
public record PatientCodeResponse(
        @JsonProperty("p_code") Integer pCode
) {
}
