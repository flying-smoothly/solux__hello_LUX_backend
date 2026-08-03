package com.eum.hello_lux_quiz.domain.member.dto;

/**
 * 아이디 중복확인 응답. 사용 가능하면 available=true.
 * 예: {"available":true}
 */
public record CheckIdResponse(
        boolean available
) {
}