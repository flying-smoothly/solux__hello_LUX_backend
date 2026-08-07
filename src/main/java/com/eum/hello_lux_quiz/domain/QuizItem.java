package com.eum.hello_lux_quiz.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "퀴즈응시")
@IdClass(QuizItemId.class)
public class QuizItem {

    @Id
    @Column(name = "quiz_num", nullable = false)
    private Integer quizNum;

    @Id
    @Column(name = "set_id", nullable = false)
    @JsonProperty("set_id") //  JSON Key 이름을 "set_id"로 지정
    private Integer setId;

    @Id
    @Column(name = "p_code", nullable = false)
    @JsonProperty("p_code") //  JSON Key 이름을 "p_code"로 명시적 지정
    private Integer pCode;

    @Column(name = "score")
    private Integer score = 0;

    @Column(name = "quiz_category", nullable = false, length = 50)
    private String quizCategory;

    @Column(name = "level", nullable = false)
    private Integer level;

    @Column(name = "quiz_comment", columnDefinition = "TEXT")
    private String quizComment;

    @Column(name = "quiz_photo", columnDefinition = "TEXT")
    private String quizPhoto;

    @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
    private String answer;

    @Column(name = "options", columnDefinition = "TEXT")
    private String options;

    @Column(name = "hints", columnDefinition = "TEXT")
    private String hints;

    protected QuizItem() {
    }

    public QuizItem(Integer setId, Integer pCode, Integer quizNum, String quizCategory, Integer level, String quizComment, String quizPhoto, String answer, String options) {
        this(setId, pCode, quizNum, quizCategory, level, quizComment, quizPhoto, answer, options, null);
    }

    public QuizItem(Integer setId, Integer pCode, Integer quizNum, String quizCategory, Integer level, String quizComment, String quizPhoto, String answer, String options, String hints) {
        this.setId = setId;
        this.pCode = pCode;
        this.quizNum = quizNum;
        this.quizCategory = quizCategory;
        this.level = level;
        this.quizComment = quizComment;
        this.quizPhoto = quizPhoto;
        this.answer = answer;
        this.options = options;
        this.hints = hints;
        this.score = 0;
    }

    // --- Getter ---
    public Integer getSetId() {
        return setId;
    }

    // Getter를 하나만 남깁니다. (@JsonProperty 덕분에 "p_code"로 변환됩니다)
    public Integer getpCode() {
        return pCode;
    }

    public Integer getQuizNum() {
        return quizNum;
    }

    public Integer getScore() {
        return score;
    }

    public String getQuizCategory() {
        return quizCategory;
    }

    public Integer getLevel() {
        return level;
    }

    public String getQuizComment() {
        return quizComment;
    }

    public String getQuizPhoto() {
        return quizPhoto;
    }

    public String getAnswer() {
        return answer;
    }

    public String getOptions() {
        return options;
    }

    public String getHints() {
        return hints;
    }
}
