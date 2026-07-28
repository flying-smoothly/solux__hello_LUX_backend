package com.eum.hello_lux_quiz.domain.patient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * 환자 기본 정보 등록 요청.
 * 예: {"diagnosis":"경도인지장애","personality":"온화함","speech_style":"존댓말","gender":"남"}
 */
public record PatientRegisterRequest(
        @NotBlank(message = "진단명은 필수입니다.")
        String diagnosis,

        @NotBlank(message = "성격은 필수입니다.")
        String personality,

        @JsonProperty("speech_style")
        @NotBlank(message = "말투는 필수입니다.")
        String speechStyle,

        @NotBlank(message = "성별은 필수입니다.")
        String gender
) {
}
