package com.eum.hello_lux_quiz.domain.patient.entity;

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

import java.time.LocalDate;

/**
 * 환자가 직접 입력하는 일일 상태(건강 체크) 엔티티.
 * 프론트 S10(DailyHealthCheck) 화면의 입력값을 저장한다.
 * (건강 상태 / 수면 상태 / 식사 여부 / 통증 / 기분 / 인지 변화)
 *
 * <p>환자 1명당 하루 1건을 유지하기 위해 (p_code, record_date) 를 유일하게 관리한다.</p>
 */
@Entity
@Getter
@Table(name = "patient_daily_status",
        uniqueConstraints = @UniqueConstraint(columnNames = {"p_code", "record_date"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatientDailyStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Long id;

    @Column(name = "p_code", nullable = false)
    private Integer pCode;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    /** 건강 상태 (좋음 / 보통 / 좋지 않음) */
    @Column(name = "health_condition", length = 20)
    private String healthCondition;

    /** 수면 상태 (잘 잤음 / 보통 / 거의 못 잠) */
    @Column(name = "sleep_status", length = 20)
    private String sleepStatus;

    /** 식사 여부 (식사함 / 식사 못 함) */
    @Column(name = "meal_status", length = 20)
    private String mealStatus;

    /** 통증 / 불편감 */
    @Column(name = "pain_status", length = 20)
    private String painStatus;

    /** 기분(감정) 상태 */
    @Column(name = "mood_status", length = 20)
    private String moodStatus;

    /** 오늘 행동 및 인지 변화(중복 선택). 콤마로 join 하여 저장 */
    @Column(name = "cognitive_changes", columnDefinition = "TEXT")
    private String cognitiveChanges;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

    @Builder
    public PatientDailyStatus(Integer pCode, LocalDate recordDate, String healthCondition,
                              String sleepStatus, String mealStatus, String painStatus,
                              String moodStatus, String cognitiveChanges) {
        this.pCode = pCode;
        this.recordDate = recordDate;
        this.healthCondition = healthCondition;
        this.sleepStatus = sleepStatus;
        this.mealStatus = mealStatus;
        this.painStatus = painStatus;
        this.moodStatus = moodStatus;
        this.cognitiveChanges = cognitiveChanges;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    /** 같은 날 재입력 시 값 갱신 (하루 1건 유지). */
    public void update(String healthCondition, String sleepStatus, String mealStatus,
                       String painStatus, String moodStatus, String cognitiveChanges) {
        this.healthCondition = healthCondition;
        this.sleepStatus = sleepStatus;
        this.mealStatus = mealStatus;
        this.painStatus = painStatus;
        this.moodStatus = moodStatus;
        this.cognitiveChanges = cognitiveChanges;
        this.updatedAt = LocalDate.now();
    }
}