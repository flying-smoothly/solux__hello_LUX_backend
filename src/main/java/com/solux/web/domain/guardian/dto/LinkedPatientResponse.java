package com.solux.web.domain.guardian.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 연동된 환자 목록 항목.
 * 예: {"p_code":1001,"name":"홍길동","last_score":85}
 * last_score(최근 점수)는 퀴즈 모듈 연동 시 채워진다. 미연동 시 null.
 */
public record LinkedPatientResponse(
        @JsonProperty("p_code") Integer pCode,
        String name,
        @JsonProperty("last_score") Integer lastScore
) {
}
