package com.eum.hello_lux_quiz.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.eum.hello_lux_quiz.domain.member.entity.Member;

import java.time.LocalDate;

/**
 * 현재 로그인한 회원 정보 조회 응답.
 * role 은 역할 선택 전이면 null, p_code 는 환자로 등록되지 않았으면 null 이다.
 * 예: {"user_id":"hong1234","name":"홍길동","birth_date":"1950-01-01","phone":"01012345678","role":"patient","p_code":"AB37X2"}
 */
public record MeResponse(
        @JsonProperty("user_id") String userId,
        String name,
        @JsonProperty("birth_date") LocalDate birthDate,
        String phone,
        String role,
        @JsonProperty("p_code") String pCode
) {
    public static MeResponse of(Member member, String patientCode) {
        return new MeResponse(
                member.getUserId(),
                member.getName(),
                member.getBirthDate(),
                member.getPhone(),
                member.getRole() != null ? member.getRole().getValue() : null,
                patientCode
        );
    }
}