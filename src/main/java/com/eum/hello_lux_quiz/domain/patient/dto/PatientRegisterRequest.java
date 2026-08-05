package com.eum.hello_lux_quiz.domain.patient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * 환자 기본 정보 등록 요청.
 * 예: {"diagnosis":"경도인지장애","gender":"남","cognitive_support_level":"보통","guardian_companion":true}
 * speech_style 은 음성 설정(S06) 화면에서 채우는 걸로. 
 */
public record PatientRegisterRequest(
        @NotBlank(message = "진단명은 필수입니다.")
        String diagnosis,

        @NotBlank(message = "성별은 필수입니다.")
        String gender,

        /** 인지 지원 수준(낮음/보통/높음) */
        @JsonProperty("cognitive_support_level")
        String cognitiveSupportLevel,

        /** 보호자 동행 여부 */
        @JsonProperty("guardian_companion")
        Boolean guardianCompanion,

        /** 성격(선택) */
        String personality,

        /** 말투(선택, 음성 설정 화면에서 입력) */
        String speechStyle
) {
}
