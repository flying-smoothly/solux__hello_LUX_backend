package com.eum.hello_lux_quiz.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 로그인 성공 응답. role 은 역할 선택 전이면 null 이다.
 * 예: {"token":"jwt_token_here","role":"patient","user_id":"hong1234","name":"홍길동"}
 */
public record LoginResponse(
        String token,
        String role,
        @JsonProperty("user_id") String userId,
        String name
) {
}
