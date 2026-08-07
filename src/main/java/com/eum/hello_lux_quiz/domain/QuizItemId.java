package com.eum.hello_lux_quiz.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * 퀴즈응시(QuizItem) 엔티티의 복합키 식별자 클래스
 */
public class QuizItemId implements Serializable {

    private Integer quizNum;
    private Integer setId;
    private Integer pCode;

    // JPA용 기본 생성자
    public QuizItemId() {
    }

    // 모든 필드를 받는 생성자
    public QuizItemId(Integer quizNum, Integer setId, Integer pCode) {
        this.quizNum = quizNum;
        this.setId = setId;
        this.pCode = pCode;
    }

    // Getter
    public Integer getQuizNum() {
        return quizNum;
    }

    public Integer getSetId() {
        return setId;
    }

    public Integer getPCode() {
        return pCode;
    }

    // 복합키 동등성 비교용 equals & hashCode (필수)
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QuizItemId that = (QuizItemId) o;
        return Objects.equals(quizNum, that.quizNum)
                && Objects.equals(setId, that.setId)
                && Objects.equals(pCode, that.pCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quizNum, setId, pCode);
    }
}
