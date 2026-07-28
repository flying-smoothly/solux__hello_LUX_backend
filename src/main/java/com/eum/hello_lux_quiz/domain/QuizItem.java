package com.eum.hello_lux_quiz.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "퀴즈응시",
        uniqueConstraints = @UniqueConstraint(columnNames = {"set_id", "quiz_num"}))
public class QuizItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    private Integer quizId;

    @Column(name = "set_id", nullable = false)
    private Integer setId;

    @Column(name = "p_code", nullable = false)
    private Integer pCode;

    @Column(name = "quiz_num", nullable = false)
    private Integer quizNum;

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

    // 수동 생성자
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

    public Integer getSetId() {
        return setId;
    }

    // quiz_llm 원본 누락 getter 보강: QuizService.java 에서 item.getQuizNum() 호출하나
    // QuizItem 에 정의가 없어 컴파일이 실패하던 기존 버그 수정
    public Integer getQuizNum() {
        return quizNum;
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
