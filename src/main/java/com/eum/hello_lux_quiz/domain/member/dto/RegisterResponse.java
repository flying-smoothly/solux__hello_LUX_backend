package com.eum.hello_lux_quiz.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.eum.hello_lux_quiz.domain.member.entity.Member;

/**
 * 회원가입 성공 응답.
 * 예: {"user_id":"hong1234","name":"홍길동"}
 *  */
public record RegisterResponse(
        @JsonProperty("user_id") String userId,
        String role,
        String name
) {
    public static RegisterResponse from(Member member) {
        return new RegisterResponse(
                member.getUserId(),
                member.getRole() != null ? member.getRole().name() : null,
                member.getName()
        );
    }
}
