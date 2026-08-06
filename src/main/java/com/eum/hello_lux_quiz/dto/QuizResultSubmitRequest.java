package com.eum.hello_lux_quiz.dto;

public class QuizResultSubmitRequest {

    private Integer setId;
    private Integer pCode;
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
    public QuizResultSubmitRequest(Integer setId, Integer pCode, Integer totalCount, Integer correctCount, Integer hint, String caculate, String feedbackContent) {
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

    public Integer getPCode() {
        return pCode;
    }

    public void setPCode(Integer pCode) {
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
