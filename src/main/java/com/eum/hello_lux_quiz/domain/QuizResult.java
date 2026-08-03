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

        // === 프론트 연동 위함 (S19) ===

        @Column(name = "success_rate")
        private Integer successRate;      // 답변 성공률(%) — 미지정 시 correct/total 로 계산

        @Column(name = "hint_used")
        private Boolean hintUsed;         // 힌트 사용 여부 — 미지정 시 hint > 0 으로 계산

        @Column(name = "avg_response_time")
        private Integer avgResponseTime;  // 평균 응답 시간(초)

        @Column(name = "health_status", length = 20)
        private String healthStatus;      // 건강 상태

        @Column(name = "sleep_status", length = 20)
        private String sleepStatus;       // 수면 상태

        @Column(name = "emotion_status", length = 20)
        private String emotionStatus;     // 감정(기분) 상태

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
            this.successRate = calcSuccessRate(totalCount, correctCount);
            this.hintUsed = hint > 0;
        }

        /**
         * 확장 필드까지 포함하는 생성자. successRate/hintUsed 가 null 이면 기본 계산값을 사용한다.
         */
        public QuizResult(Integer setId, Integer pCode, LocalDate date, int totalCount, int correctCount,
                          int hint, String caculate, Integer successRate, Boolean hintUsed,
                          Integer avgResponseTime, String healthStatus, String sleepStatus, String emotionStatus) {
            this.setId = setId;
            this.pCode = pCode;
            this.date = date;
            this.totalCount = totalCount;
            this.correctCount = correctCount;
            this.hint = hint;
            this.caculate = caculate;
            this.successRate = (successRate != null) ? successRate : calcSuccessRate(totalCount, correctCount);
            this.hintUsed = (hintUsed != null) ? hintUsed : (hint > 0);
            this.avgResponseTime = avgResponseTime;
            this.healthStatus = healthStatus;
            this.sleepStatus = sleepStatus;
            this.emotionStatus = emotionStatus;
        }

        private static Integer calcSuccessRate(int totalCount, int correctCount) {
            if (totalCount <= 0) {
                return 0;
            }
            return Math.round(correctCount * 100f / totalCount);
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

        public Integer getSuccessRate() {
            return successRate;
        }

        public Boolean getHintUsed() {
            return hintUsed;
        }

        public Integer getAvgResponseTime() {
            return avgResponseTime;
        }

        public String getHealthStatus() {
            return healthStatus;
        }

        public String getSleepStatus() {
            return sleepStatus;
        }

        public String getEmotionStatus() {
            return emotionStatus;
        }
    }

                                                                                                                                                                            
          
          
          
          
          
          
          