package com.eum.hello_lux_quiz.domain.doctor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 진료 참고 리포트 조회 응답.
 * 예: {"avg_score":82,"trend":"상승","feedback_list":[]}
 * avg_score/trend 는 퀴즈 결과 집계에, feedback_list 는 퀴즈 피드백에 의존한다.
 */
public record ReportResponse(
        @JsonProperty("avg_score") Integer avgScore,
        String trend,
        @JsonProperty("feedback_list") List<String> feedbackList
) {
}
