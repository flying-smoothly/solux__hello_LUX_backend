package com.eum.hello_lux_quiz.domain.guardian.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 보호자가 (연동된) 환자 정보를 입력/수정하는 요청. 전달된 필드만 반영된다.
 * 예: {"gender":"남","diagnosis":"경도인지장애","personality":"온화함","speech_style":"존댓말"}
 */
public record PatientInfoRequest(
        String gender,
        String diagnosis,
        String personality,

        @JsonProperty("speech_style")
        String speechStyle
) {
}