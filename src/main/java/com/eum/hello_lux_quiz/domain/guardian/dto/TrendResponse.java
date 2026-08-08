package com.eum.hello_lux_quiz.domain.guardian.dto;

import java.util.List;

/**
 * 변화 추이 조회 응답.
 * 예: {"labels":["5/1","5/8","5/15"],"scores":[75,80,85]}
 * 퀴즈 결과 집계에 의존하므로 퀴즈 모듈 연동 전에는 빈 배열이 반환된다.
 */
public record TrendResponse(
        List<String> labels,
        List<Integer> scores
) {
}
