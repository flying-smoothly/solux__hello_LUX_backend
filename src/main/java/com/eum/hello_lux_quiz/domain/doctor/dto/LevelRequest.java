package com.eum.hello_lux_quiz.domain.doctor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 난이도 조절 요청.
 * 예: {"quiz_type":"multiple","level":3}
 */
public record LevelRequest(
        @JsonProperty("quiz_type")
        @NotBlank(message = "퀴즈 유형(quiz_type)은 필수입니다.")
        String quizType,

        @NotNull(message = "레벨(level)은 필수입니다.")
        Integer level
) {
}
