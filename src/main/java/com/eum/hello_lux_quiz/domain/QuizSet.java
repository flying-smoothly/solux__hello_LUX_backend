package com.eum.hello_lux_quiz.domain;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "퀴즈세트")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 규약용
public class QuizSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "set_id")
    private Integer setId; // Long -> Integer 변경

    @Column(name = "p_code", nullable = false)
    private Integer pCode; // Long -> Integer 변경

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "anti_time")
    private Integer antiTime;

    // 1. total_score 필드 추가 (기본값 0 설정)
    @Column(name = "total_score", nullable = false)
    private Integer totalScore = 0;

    // 기존 생성자 (Long -> Integer 변경)
    public QuizSet(Integer pCode, LocalTime startTime, LocalTime endTime, Integer antiTime) {
        this.pCode = pCode;
        this.startTime = startTime;
        this.endTime = endTime;
        this.antiTime = antiTime;
        this.totalScore = 0;
    }

    // 2. totalScore까지 직접 받는 생성자 (Long -> Integer 변경)
    public QuizSet(Integer pCode, LocalTime startTime, LocalTime endTime, Integer antiTime, Integer totalScore) {
        this.pCode = pCode;
        this.startTime = startTime;
        this.endTime = endTime;
        this.antiTime = antiTime;
        this.totalScore = totalScore != null ? totalScore : 0;
    }

    public Integer getSetId() { // Long -> Integer 변경
        return setId;
    }

    public Integer getpCode() { // Long -> Integer 변경
        return pCode;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Integer getAntiTime() {
        return antiTime;
    }

    // 3. Getter 추가
    public Integer getTotalScore() {
        return totalScore;
    }
}
