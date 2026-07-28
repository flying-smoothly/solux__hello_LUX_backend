package com.eum.hello_lux_quiz.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "퀴즈응시",
        uniqueConstraints = @UniqueConstraint(columnNames = {"set_id", "quiz_num"}))
public class QuizItem {

    // 전역 식별자(API 명세의 quiz_id). 세트를 가로질러 유일하다.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    private Integer quizId;

    @Column(name = "set_id", nullable = false)
    private Integer setId;

    @Column(name = "p_code", nullable = false)
    private Integer pCode;

    // 세트 내 순번(1~7). (set_id, quiz_num) 으로 힌트/문항을 식별한다.
    @Column(name = "quiz_num", nullable = false)
    private Integer quizNum;

    // 문항 점수. 생성 시점엔 미채점이므로 nullable.
    @Column(name = "score")
    private Integer score;

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
    private String options; // 객관식 보기 컬럼

    // JPA용 기본 생성자
    protected QuizItem() {
    }

    // 수동 생성자 (생성 시 세트 순번 quizNum 을 부여, 점수는 채점 시 반영)
    public QuizItem(Integer setId, Integer pCode, Integer quizNum, String quizCategory, Integer level, String quizComment, String quizPhoto, String answer, String options) {
        this.setId = setId;
        this.pCode = pCode;
        this.quizNum = quizNum;
        this.quizCategory = quizCategory;
        this.level = level;
        this.quizComment = quizComment;
        this.quizPhoto = quizPhoto;
        this.answer = answer;
        this.options = options;
    }

    // --- 수동 Getter ---
    public Integer getQuizId() {
        return quizId;
    }

    public Integer getQuizNum() {
        return quizNum;
    }

    public Integer getSetId() {
        return setId;
    }

    public Integer getPCode() {
        return pCode;
    }

    public Integer getpCode() {
        return pCode; // Lombok 네이밍 호환용
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
}
