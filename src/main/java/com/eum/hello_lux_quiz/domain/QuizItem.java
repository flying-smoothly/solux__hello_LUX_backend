package com.eum.hello_lux_quiz.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "퀴즈응시")
@IdClass(QuizItemId.class) // 👈 1번에서 만든 복합키 클래스 지정
public class QuizItem {

    @Id // 👈 복합키 1
    @Column(name = "quiz_num", nullable = false)
    private Integer quizNum;

    @Id // 👈 복합키 2
    @Column(name = "set_id", nullable = false)
    private Integer setId;

    @Id // 👈 복합키 3
    @Column(name = "p_code", nullable = false)
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

    // JPA 규약용 기본 생성자
    protected QuizItem() {
    }

    // 기존 생성자 (호환용)
    public QuizItem(Integer setId, Integer pCode, Integer quizNum, String quizCategory, Integer level, String quizComment, String quizPhoto, String answer, String options) {
        this(setId, pCode, quizNum, quizCategory, level, quizComment, quizPhoto, answer, options, null);
    }

    // 힌트 포함 생성자 (QuizService에서 사용)
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

    public Integer getPCode() {
        return pCode;
    }

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
