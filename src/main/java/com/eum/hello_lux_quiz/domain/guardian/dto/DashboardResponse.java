package com.eum.hello_lux_quiz.domain.guardian.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * 환자 요약 대시보드 응답.
 * 예: {"avg_score":82,"trend":"상승","last_quiz_date":"2026-05-29"}
 * 퀴즈 결과 집계에 의존하므로 퀴즈 모듈 연동 전에는 기본값이 반환된다.
 */
public record DashboardResponse(
        @JsonProperty("avg_score") Integer avgScore,
        String trend,
        @JsonProperty("last_quiz_date") LocalDate lastQuizDate
) {
}
