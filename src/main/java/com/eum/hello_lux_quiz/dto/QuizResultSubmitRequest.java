package com.eum.hello_lux_quiz.dto;

public class QuizResultSubmitRequest {

    private Integer setId;
    private Integer pCode;
    private Integer totalCount;
    private Integer correctCount;
    private Integer hint;
    private String caculate;
    private String feedbackContent;

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
}
