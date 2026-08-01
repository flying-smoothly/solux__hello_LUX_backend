package com.eum.hello_lux_quiz.domain.guardian.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 건강/수면/식사/통증/기분/감정·행동/전문기관 연계 필요여부/특이 행동 메모 보관 (보호자 -> 환자)
 */
@Entity
@Getter
@Table(name = "status_memo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StatusMemo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memo_id")
    private Long memoId;

    @Column(name = "p_code", nullable = false)
    private Integer pCode;

    @Column(name = "user_email", nullable = false, length = 100)
    private String userEmail;

   /** 기록 대상 날짜 (보호자가 직접 선택) */
    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    /** 건강 상태 (좋음 / 보통 / 좋지 않음) */
    @Column(name = "health_status", length = 20)
    private String healthStatus;

    /** 수면 상태 (잘잠 / 보통 / 못 잠) */
    @Column(name = "sleep_status", length = 20)
    private String sleepStatus;

    /** 식사 (식사함 / 식사 못 함) */
    @Column(name = "meal_status", length = 20)
    private String mealStatus;

    /** 통증 / 불편감 (없음 / 있음) */
    @Column(name = "pain_status", length = 20)
    private String painStatus;

    /** 기분 상태 (안정 / 불안 / 우울 / 화남 / 무기력) */
    @Column(name = "mood_status", length = 20)
    private String moodStatus;

    /** 감정·행동 기록(중복 선택). 콤마로 join 하여 저장 */
    @Column(name = "behaviors", columnDefinition = "TEXT")
    private String behaviors;

    /** 전문기관 연계 필요 여부 */
    @Column(name = "need_referral", nullable = false)
    private boolean needReferral;

    /** 특이 행동 메모(선택) */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

    @Builder
   public StatusMemo(Integer pCode, String userEmail, LocalDate recordDate,
                      String healthStatus, String sleepStatus, String mealStatus,
                      String painStatus, String moodStatus, String behaviors,
                      boolean needReferral, String content) {
        this.pCode = pCode;
        this.userEmail = userEmail;
        this.recordDate = (recordDate != null) ? recordDate : LocalDate.now();
        this.healthStatus = healthStatus;
        this.sleepStatus = sleepStatus;
        this.mealStatus = mealStatus;
        this.painStatus = painStatus;
        this.moodStatus = moodStatus;
        this.behaviors = behaviors;
        this.needReferral = needReferral;
        this.content = content;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    public void update(LocalDate recordDate, String healthStatus, String sleepStatus,
                       String mealStatus, String painStatus, String moodStatus,
                       String behaviors, boolean needReferral, String content) {
        if (recordDate != null) {
            this.recordDate = recordDate;
        }
        this.healthStatus = healthStatus;
        this.sleepStatus = sleepStatus;
        this.mealStatus = mealStatus;
        this.painStatus = painStatus;
        this.moodStatus = moodStatus;
        this.behaviors = behaviors;
        this.needReferral = needReferral;
        this.content = content;
        this.updatedAt = LocalDate.now();
    }
}
