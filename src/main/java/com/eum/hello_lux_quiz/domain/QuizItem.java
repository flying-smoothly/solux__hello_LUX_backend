package com.eum.hello_lux_quiz.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "퀴즈응시")
public class QuizItem {

    // DB의 quiz_num 컬럼이 PK이자 auto_increment입니다.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_num")
    private Long quizNum;

    @Column(name = "set_id", nullable = false)
    private Integer setId;

    @Column(name = "p_code", nullable = false)
    private Integer pCode;

    @Column(name = "score", nullable = false)
    private Integer score = 0; // score NOT NULL 방지 기본값

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

    // JPA용 기본 생성자
    protected QuizItem() {
    }

    // 수동 생성자 (quizNum은 DB가 자동 생성하므로 제외)
    public QuizItem(Integer setId, Integer pCode, String quizCategory, Integer level, String quizComment, String quizPhoto, String answer, String options, String hints) {
        this.setId = setId;
        this.pCode = pCode;
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
    public Long getQuizNum() {
        return quizNum;
    }

    public Integer getSetId() {
        return setId;
    }

    public Integer getPCode() {
        return pCode;
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
