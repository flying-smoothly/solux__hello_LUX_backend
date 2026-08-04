package com.eum.hello_lux_quiz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoiceSetting {

    @Builder.Default
    private Double ttsSpeed = 1.0;          // TTS 속도 (기본값: 1.0)

    @Builder.Default
    private String sentenceLength = "보통"; // 문장 길이 (짧게 / 보통 / 길게)

    @Builder.Default
    private Boolean isHonorific = true;     // 존댓말 여부 (기본값: true)

    @Builder.Default
    private Boolean isAutoPlay = false;     // 자동재생 여부 (기본값: false)

    @Builder.Default
    private Boolean isRepeatGuide = true;   // 반복안내 여부 (기본값: true)

    @Builder.Default
    private Boolean isLowPressure = true;   // 압박감 낮춤 여부 (기본값: true)

    @Builder.Default
    private Boolean isPositiveFeedback = true; // 긍정 피드백 여부 (기본값: true)

    private String speechStyle;             // 말투 / 어조 (음성 설정 화면 통합)

    /**
     * 기존 VoiceSetting을 바탕으로 새로운 설정값을 전달받아 업데이트(수정)하는 생성자
     */
    public VoiceSetting(VoiceSetting original, Double ttsSpeed, String sentenceLength, 
                        Boolean isHonorific, Boolean isAutoPlay, Boolean isRepeatGuide, 
                        Boolean isLowPressure, Boolean isPositiveFeedback, String speechStyle) {
        this.ttsSpeed = (ttsSpeed != null) ? ttsSpeed : (original != null ? original.getTtsSpeed() : 1.0);
        this.sentenceLength = (sentenceLength != null) ? sentenceLength : (original != null ? original.getSentenceLength() : "보통");
        this.isHonorific = (isHonorific != null) ? isHonorific : (original != null ? original.getIsHonorific() : true);
        this.isAutoPlay = (isAutoPlay != null) ? isAutoPlay : (original != null ? original.getIsAutoPlay() : false);
        this.isRepeatGuide = (isRepeatGuide != null) ? isRepeatGuide : (original != null ? original.getIsRepeatGuide() : true);
        this.isLowPressure = (isLowPressure != null) ? isLowPressure : (original != null ? original.getIsLowPressure() : true);
        this.isPositiveFeedback = (isPositiveFeedback != null) ? isPositiveFeedback : (original != null ? original.getIsPositiveFeedback() : true);
        this.speechStyle = (speechStyle != null) ? speechStyle : (original != null ? original.getSpeechStyle() : null);
    }

    /**
     * 객체 내부에서 값을 안전하게 갱신하는 수정 메서드
     */
    public void updateSetting(Double ttsSpeed, String sentenceLength, Boolean isHonorific,
                              Boolean isAutoPlay, Boolean isRepeatGuide, Boolean isLowPressure,
                              Boolean isPositiveFeedback, String speechStyle) {
        if (ttsSpeed != null) this.ttsSpeed = ttsSpeed;
        if (sentenceLength != null) this.sentenceLength = sentenceLength;
        if (isHonorific != null) this.isHonorific = isHonorific;
        if (isAutoPlay != null) this.isAutoPlay = isAutoPlay;
        if (isRepeatGuide != null) this.isRepeatGuide = isRepeatGuide;
        if (isLowPressure != null) this.isLowPressure = isLowPressure;
        if (isPositiveFeedback != null) this.isPositiveFeedback = isPositiveFeedback;
        if (speechStyle != null) this.speechStyle = speechStyle;
    }
}
