package com.eum.hello_lux_quiz.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * 개인정보 수정 요청. 모든 필드는 선택이며 전달된 값만 반영된다.
 * 예: {"name":"홍길동","birth_date":"1950-01-01","user_pw":"new1234"}
 */
public record ProfileUpdateRequest(
        String name,

        @JsonProperty("birth_date")
        LocalDate birthDate,

        @JsonProperty("user_pw")
        String userPw
) {
}
