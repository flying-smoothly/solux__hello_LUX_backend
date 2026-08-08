package com.eum.hello_lux_quiz.domain.doctor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 환자별 퀴즈 난이도 설정. 의사가 조절하며, 퀴즈 모듈이 문제 출제 시 참조한다.
 * (p_code, quiz_type) 당 하나의 레벨을 유지한다.
 */
@Entity
@Getter
@Table(name = "patient_quiz_level",
        uniqueConstraints = @UniqueConstraint(columnNames = {"p_code", "quiz_type"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatientQuizLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "level_id")
    private Long id;

    @Column(name = "p_code", nullable = false)
    private Integer pCode;

    @Column(name = "quiz_type", nullable = false, length = 20)
    private String quizType;

    @Column(name = "level", nullable = false)
    private Integer level;

    @Builder
    public PatientQuizLevel(Integer pCode, String quizType, Integer level) {
        this.pCode = pCode;
        this.quizType = quizType;
        this.level = level;
    }

    public void updateLevel(Integer level) {
        this.level = level;
    }
}
