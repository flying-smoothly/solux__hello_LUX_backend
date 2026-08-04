package com.eum.hello_lux_quiz.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "퀴즈_결과")
public class QuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;           // bigint(20)

    @Column(name = "set_id", nullable = false)
    private Integer setId;           // int(11)

    @Column(name = "p_code", nullable = false)
    private Integer pCode;           // int(11)

    @Column(name = "date", nullable = false)
    private LocalDate date;          // date

    @Column(name = "result_date", nullable = false)
    private LocalDate resultDate;    // date

    @Column(name = "total_count", nullable = false)
    private int totalCount;          // int(11)

    @Column(name = "correct_count", nullable = false)
    private int correctCount;        // int(11)

    @Column(name = "hint", nullable = false)
    private int hint;                // int(11)

    @Column(name = "hint_count", nullable = false)
    private int hintCount = 0;       // int(11) - 추가된 필수 컬럼

    @Column(name = "caculate", columnDefinition = "TEXT", nullable = false)
    private String caculate;         // text

    @Column(name = "trend_calc", columnDefinition = "TEXT", nullable = false)
    private String trendCalc = "0";  // text - DB 타입(text)에 맞게 기본값 설정

    protected QuizResult() {
    }

    public QuizResult(Integer setId, Integer pCode, LocalDate resultDate, int totalCount, int correctCount, int hint, String caculate) {
        this.setId = setId;
        this.pCode = pCode;

        // date와 result_date 두 컬럼 모두 값 채워주기
        LocalDate today = (resultDate != null) ? resultDate : LocalDate.now();
        this.date = today;
        this.resultDate = today;

        this.totalCount = totalCount;
        this.correctCount = correctCount;
        this.hint = hint;
        this.hintCount = hint;       // hint_count 채우기
        this.caculate = caculate;
        this.trendCalc = "0";        // trend_calc 채우기
    }

    @PrePersist
    public void prePersist() {
        LocalDate today = LocalDate.now();
        if (this.date == null) {
            this.date = today;
        }
        if (this.resultDate == null) {
            this.resultDate = today;
        }
    }

    // --- Getter ---
    public Long getResultId() {
        return resultId;
    }

    public Integer getSetId() {
        return setId;
    }

    public Integer getPCode() {
        return pCode;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalDate getResultDate() {
        return resultDate;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public int getHint() {
        return hint;
    }

    public int getHintCount() {
        return hintCount;
    }

    public String getCaculate() {
        return caculate;
    }

    public String getTrendCalc() {
        return trendCalc;
    }
}
