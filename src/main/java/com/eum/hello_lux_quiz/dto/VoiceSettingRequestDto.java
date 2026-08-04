package com.eum.hello_lux_quiz.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VoiceSettingRequestDto {

    private Double ttsSpeed;             // TTS 속도
    private String sentenceLength;       // 문장 길이 (짧게 / 보통 / 길게)
    private Boolean isHonorific;        // 존댓말 여부
    private Boolean isAutoPlay;         // 자동재생 여부
    private Boolean isRepeatGuide;      // 반복안내 여부
    private Boolean isLowPressure;      // 압박감 낮춤 여부
    private Boolean isPositiveFeedback; // 긍정 피드백 여부
    private String speechStyle;         // 말투 스타일 (필요시 함께 전달)
    
    // 추가된 UI 입력 필드
    private String cognitiveLevel;      // 인지 지원 수준 (낮음 / 보통 / 높음)
    private Boolean isGuardianPresent; // 보호자 동행 여부
}
