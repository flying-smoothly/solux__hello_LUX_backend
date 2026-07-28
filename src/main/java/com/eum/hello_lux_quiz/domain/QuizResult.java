package com.eum.hello_lux_quiz.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@ Entity 

    @Table(name = "퀴즈_결과")
    public class QuizResult {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "result_id")
        private Integer resultId;        // 결과코드 (PK)

        @Column(name = "set_id", nullable = false)
        private Integer setId;           // 퀴즈코드 (FK)

        @Column(name = "p_code", nullable = false)
        private Integer pCode;           // 환자코드 (FK)

        @Column(name = "result_date", nullable = false)
        private LocalDate date;       // 날짜 (DATE 타입)

        @Column(name = "total_count", nullable = false)
        private int totalCount;       // 총_문제_수

        @Column(name = "correct_count", nullable = false)
        private int correctCount;     // 맞춘_수

        @Column(name = "hint_count", nullable = false)
        private int hint;             // 힌트_사용횟수

        @Column(name = "trend_calc", columnDefinition = "TEXT", nullable = false)
        private String caculate;      // 변화_계산

        protected QuizResult() {
        }

        public QuizResult(Integer setId, Integer pCode, LocalDate date, int totalCount, int correctCount, int hint, String caculate) {
            this.setId = setId;
            this.pCode = pCode;
            this.date = date;
            this.totalCount = totalCount;
            this.correctCount = correctCount;
            this.hint = hint;
            this.caculate = caculate;
        }

        @PrePersist
        public void prePersist() {
            if (this.date == null) {
                this.date = LocalDate.now();
            }
        }

        // --- 수동 Getter ---
        public Integer getResultId() {
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

        public int getTotalCount() {
            return totalCount;
        }

        public int getCorrectCount() {
            return correctCount;
        }

        public int getHint() {
            return hint;
        }

        public String getCaculate() {
            return caculate;
        }
    }

                                                                                                                                                                            
          
          
          
          
          
          
          