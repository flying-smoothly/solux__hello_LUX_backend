package com.eum.hello_lux_quiz.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class QuizItemDto {

    private Integer quizId;       // 전역 퀴즈 식별자 (quiz_id)
    private int quizNum;          // 퀴즈 번호 (1~7)
    private int level;            // 난이도
    private String quizCategory;  // 퀴즈 유형 (text / photo)
    private String quizComment;   // 퀴즈 문제/설명 텍스트
    private String quizPhoto;     // 사진 URL (사진 기반 퀴즈일 경우)
    private String answer;        // 정답
    private List<String> options; // 객관식 보기 (Level 1 전용)
    private List<String> hints;   // 퀴즈 힌트 목록 (hints)

    @JsonProperty("set_id")
    private Integer setId;

    @JsonProperty("p_code")
    private String pCode;

    // ========================================================
    //  IDE Lombok 인식 오류 및 타입 불일치 방지용 수동 Getter / Setter
    // ========================================================
    public Integer getQuizId() {
        return quizId;
    }

    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }

    public int getQuizNum() {
        return quizNum;
    }

    public void setQuizNum(Object quizNum) {
        if (quizNum instanceof Number) {
            this.quizNum = ((Number) quizNum).intValue();
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(Object level) {
        if (level instanceof Number) {
            this.level = ((Number) level).intValue();
        }
    }

    public String getQuizCategory() {
        return quizCategory;
    }

    public void setQuizCategory(String quizCategory) {
        this.quizCategory = quizCategory;
    }

    public String getQuizComment() {
        return quizComment;
    }

    public void setQuizComment(String quizComment) {
        this.quizComment = quizComment;
    }

    public String getQuizPhoto() {
        return quizPhoto;
    }

    public void setQuizPhoto(String quizPhoto) {
        this.quizPhoto = quizPhoto;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    //  hints 수동 Getter / Setter 추가
    public List<String> getHints() {
        return hints;
    }

    public void setHints(List<String> hints) {
        this.hints = hints;
    }

    public Integer getSetId() {
        return setId;
    }

    public void setSetId(Integer setId) {
        this.setId = setId;
    }

    public String getPCode() {
        return pCode;
    }

    public void setPCode(String pCode) {
        this.pCode = pCode;
    }
}
