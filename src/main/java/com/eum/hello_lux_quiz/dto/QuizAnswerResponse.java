package com.eum.hello_lux_quiz.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuizAnswerResponse {

    private boolean isCorrect; // 💡 정답 여부 추가 (true / false)
    private String feedback;   // 피드백 문구

    // 1. 피드백만 받는 기존 생성자 (하위 호환용)
    public QuizAnswerResponse(String feedback) {
        this.feedback = feedback;
    }

    // 2. 정답 여부 + 피드백을 함께 받는 생성자 (채점 서비스용)
    public QuizAnswerResponse(boolean isCorrect, String feedback) {
        this.isCorrect = isCorrect;
        this.feedback = feedback;
    }
}
