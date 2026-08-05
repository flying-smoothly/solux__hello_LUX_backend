package com.eum.hello_lux_quiz.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class QuizResultSubmitRequest {

    private Integer setId;
    /**
     * 6자리 연동 코드. 명시하지 않으면 Jackson 이 "pcode" 로 매핑하므로 키를 고정한다.
     * 정수 식별자를 보내던 구버전 클라이언트를 위해 "pcode" 도 함께 받는다.
     */
    @JsonProperty("p_code")
    @JsonAlias({"pcode", "patient_code"})
    private String pCode;
    private Integer totalCount;
    private Integer correctCount;
    private Integer hint;
    private String caculate;
    private String feedbackContent;

    // === 퀴즈 상세 정보, 건강 상태, 수면 상태, 감정 상태 추가 ===
    private Integer successRate;      // 답변 성공률(%) — 미지정 시 서버가 계산
    private Boolean hintUsed;         // 힌트 사용 여부 — 미지정 시 hint>0 으로 계산
    private Integer avgResponseTime;  // 평균 응답 시간(초)
    private String healthStatus;      // 건강 상태
    private String sleepStatus;       // 수면 상태
    private String emotionStatus;     // 감정(기분) 상태

    // 기본 수동 생성자 (Jackson JSON 파싱용)
    public QuizResultSubmitRequest() {
    }

    // 전체 필드 수동 생성자
    public QuizResultSubmitRequest(Integer setId, String pCode, Integer totalCount, Integer correctCount, Integer hint, String caculate, String feedbackContent) {
        this.setId = setId;
        this.pCode = pCode;
        this.totalCount = totalCount;
        this.correctCount = correctCount;
        this.hint = hint;
        this.caculate = caculate;
        this.feedbackContent = feedbackContent;
    }

    // 수동 Getter & Setter
    public Integer getSetId() {
        return setId;
    }

    public void setSetId(Integer setId) {
        this.setId = setId;
    }

    public String getPCode() {
        return pCode;
    }

    public void setPCode(String pCode) {
        this.pCode = pCode;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(Integer correctCount) {
        this.correctCount = correctCount;
    }

    public Integer getHint() {
        return hint;
    }

    public void setHint(Integer hint) {
        this.hint = hint;
    }

    public String getCaculate() {
        return caculate;
    }

    public void setCaculate(String caculate) {
        this.caculate = caculate;
    }

    public String getFeedbackContent() {
        return feedbackContent;
    }

    public void setFeedbackContent(String feedbackContent) {
        this.feedbackContent = feedbackContent;
    }

     public Integer getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(Integer successRate) {
        this.successRate = successRate;
    }

    public Boolean getHintUsed() {
        return hintUsed;
    }

    public void setHintUsed(Boolean hintUsed) {
        this.hintUsed = hintUsed;
    }

    public Integer getAvgResponseTime() {
        return avgResponseTime;
    }

    public void setAvgResponseTime(Integer avgResponseTime) {
        this.avgResponseTime = avgResponseTime;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getSleepStatus() {
        return sleepStatus;
    }

    public void setSleepStatus(String sleepStatus) {
        this.sleepStatus = sleepStatus;
    }

    public String getEmotionStatus() {
        return emotionStatus;
    }

    public void setEmotionStatus(String emotionStatus) {
        this.emotionStatus = emotionStatus;
    }
}
