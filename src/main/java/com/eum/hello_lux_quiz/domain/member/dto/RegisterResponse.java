package com.eum.hello_lux_quiz.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.eum.hello_lux_quiz.domain.member.entity.Member;

/**
 * 회원가입 성공 응답.
 *  */
public record RegisterResponse(
        String token,
        @JsonProperty("user_id") String userId,
        String role,
        String name
) {
    public static RegisterResponse of(Member member, String token) {
        return new RegisterResponse(
            token,
            member.getUserId(),
            member.getRole() != null ? member.getRole().name() : null,
            member.getName()
    );
    }
}
