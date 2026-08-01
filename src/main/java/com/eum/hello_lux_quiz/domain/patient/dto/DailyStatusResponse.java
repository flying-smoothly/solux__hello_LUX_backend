package com.eum.hello_lux_quiz.domain.patient.dto;

import com.eum.hello_lux_quiz.domain.patient.entity.PatientDailyStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 환자 일일 상태 조회 응답. 저장된 인지 변화(콤마 문자열)는 리스트로 복원해 전달한다.
 */
public record DailyStatusResponse(
        @JsonProperty("status_id") Long statusId,
        @JsonProperty("p_code") Integer pCode,
        @JsonProperty("record_date") LocalDate recordDate,
        @JsonProperty("health_condition") String healthCondition,
        @JsonProperty("sleep_status") String sleepStatus,
        @JsonProperty("meal_status") String mealStatus,
        @JsonProperty("pain_status") String painStatus,
        @JsonProperty("mood_status") String moodStatus,
        @JsonProperty("cognitive_changes") List<String> cognitiveChanges
) {
    public static DailyStatusResponse from(PatientDailyStatus status) {
        return new DailyStatusResponse(
                status.getId(),
                status.getPCode(),
                status.getRecordDate(),
                status.getHealthCondition(),
                status.getSleepStatus(),
                status.getMealStatus(),
                status.getPainStatus(),
                status.getMoodStatus(),
                toList(status.getCognitiveChanges())
        );
    }

    private static List<String> toList(String joined) {
        if (joined == null || joined.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(joined.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}