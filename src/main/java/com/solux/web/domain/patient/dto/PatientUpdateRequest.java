package com.solux.web.domain.patient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 환자 정보 수정 요청. 전달된 필드만 반영된다.
 * 예: {"personality":"활발함","speech_style":"반말"}
 */
public record PatientUpdateRequest(
        String personality,

        @JsonProperty("speech_style")
        String speechStyle
) {
}
