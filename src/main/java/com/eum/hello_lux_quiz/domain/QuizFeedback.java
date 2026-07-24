package com.eum.hello_lux_quiz.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@ Entity 

    @Table(name = "퀴즈_피드백")
    public class QuizFeedback {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "feedback_id")
        private Integer feedbackId;

        @Column(name = "set_id", nullable = false)
        private Integer setId;

        @Column(name = "feedback_content", columnDefinition = "TEXT", nullable = false)
        private String feedbackContent;

        @Column(name = "created_at", nullable = false)
        private LocalDate createdAt;

        protected QuizFeedback() {
        }

        public QuizFeedback(Integer setId, String feedbackContent, LocalDate createdAt) {
            this.setId = setId;
            this.feedbackContent = feedbackContent;
            this.createdAt = createdAt;
        }

        public Integer getFeedbackId() {
            return feedbackId;
        }

        public Integer getSetId() {
            return setId;
        }

        public String getFeedbackContent() {
            return feedbackContent;
        }

        public LocalDate getCreatedAt() {
            return createdAt;
        }
    }

                                                                                            