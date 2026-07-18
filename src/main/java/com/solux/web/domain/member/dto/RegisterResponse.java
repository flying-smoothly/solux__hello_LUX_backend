package com.solux.web.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.solux.web.domain.member.entity.Member;

/**
 * 회원가입 성공 응답.
 * 예: {"user_email":"user@email.com","role":"patient","name":"홍길동"}
 */
public record RegisterResponse(
        @JsonProperty("user_email") String userEmail,
        String role,
        String name
) {
    public static RegisterResponse from(Member member) {
        return new RegisterResponse(
                member.getEmail(),
                member.getRole().getValue(),
                member.getName()
        );
    }
}
