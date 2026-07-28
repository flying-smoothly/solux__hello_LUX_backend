package com.eum.hello_lux_quiz.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 로그인 성공 응답.
 * 예: {"token":"jwt_token_here","role":"patient","user_email":"user@email.com"}
 */
public record LoginResponse(
        String token,
        String role,
        @JsonProperty("user_email") String userEmail
) {
}
