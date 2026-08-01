package com.eum.hello_lux_quiz.domain.patient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 환자 일일 상태(건강 체크) 입력 요청. 프론트 S10 화면 입력값.
 * 예:
 * {
 *   "health_condition":"좋음",
 *   "sleep_status":"잘 잤음",
 *   "meal_status":"식사함",
 *   "pain_status":"없음",
 *   "mood_status":"편안함",
 *   "cognitive_changes":["반복 발화","망상 또는 불안"]
 * }
 */
public record DailyStatusRequest(
        @JsonProperty("health_condition") String healthCondition,
        @JsonProperty("sleep_status") String sleepStatus,
        @JsonProperty("meal_status") String mealStatus,
        @JsonProperty("pain_status") String painStatus,
        @JsonProperty("mood_status") String moodStatus,
        @JsonProperty("cognitive_changes") List<String> cognitiveChanges
) {
}