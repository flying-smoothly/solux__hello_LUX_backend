package com.eum.hello_lux_quiz.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VoiceSetting {

    private Double ttsSpeed = 1.0;          // TTS 속도 (기본값: 1.0)

    private String sentenceLength = "보통"; // 문장 길이 (짧게 / 보통 / 길게)

    private Boolean isHonorific = true;     // 존댓말 여부 (기본값: true)

    private Boolean isAutoPlay = false;     // 자동재생 여부 (기본값: false)

    private Boolean isRepeatGuide = true;   // 반복안내 여부 (기본값: true)

    private Boolean isLowPressure = true;   // 압박감 낮춤 여부 (기본값: true)

    private Boolean isPositiveFeedback = true; // 긍정 피드백 여부 (기본값: true)

    private String speechStyle;             // 말투 / 어조 (음성 설정 화면 통합)

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
