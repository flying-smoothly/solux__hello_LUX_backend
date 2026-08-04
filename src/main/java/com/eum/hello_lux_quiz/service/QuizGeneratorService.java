package com.eum.hello_lux_quiz.service;

import com.eum.hello_lux_quiz.domain.PatientProfile;
import com.eum.hello_lux_quiz.domain.VoiceSetting;
import com.eum.hello_lux_quiz.dto.GeneratedQuizItemDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class QuizGeneratorService {

    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;

    public QuizGeneratorService(OpenAiClient openAiClient, ObjectMapper objectMapper) {
        this.openAiClient = openAiClient;
        this.objectMapper = objectMapper;
    }

    /**
     * resources/prompt.txt 파일 읽어오기
     */
    private String loadPromptTemplate() {
        try {
            ClassPathResource resource = new ClassPathResource("prompt.txt");
            try (InputStream is = resource.getInputStream()) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            throw new RuntimeException("프롬프트 템플릿 파일을 읽는데 실패했습니다.", e);
        }
    }

    /**
     * LLM을 활용한 맞춤형 7문항 퀴즈 생성
     */
    public List<GeneratedQuizItemDto> generateQuizSet(
            String patientStatus,
            PatientProfile profile,
            String lifeDbContext,
            String timeOrientationInstruction) {

        // 1. prompt.txt 파일 불러오기
        String promptTemplate = loadPromptTemplate();

        // 2. 환자 기본 정보 추출 (PatientProfile에 없는 필드는 기본값 적용)
        String patientNameStr = "어르신"; // PatientProfile에 별도 이름 필드가 없으므로 기본 호칭 지정
        String personalityStr = (profile != null && profile.getPersonality() != null) ? profile.getPersonality() : "다정한";
        String styleStr = "친근한 말투";
        String lifeDbStr = (lifeDbContext != null) ? lifeDbContext : "";
        String timeInstructionStr = (timeOrientationInstruction != null) ? timeOrientationInstruction : "";

        // 3. 음성/개인화 설정값 추출 (VoiceSetting 객체 사용)
        VoiceSetting voice = (profile != null) ? profile.getVoiceSetting() : null;

        String sentenceLengthStr = (voice != null && voice.getSentenceLength() != null) ? voice.getSentenceLength() : "보통";

        Boolean isHonorific = (voice != null) ? voice.getIsHonorific() : true;
        String isHonorificStr = Boolean.TRUE.equals(isHonorific) ? "존댓말 사용" : "편안한 어투";

        Boolean isRepeatGuide = (voice != null) ? voice.getIsRepeatGuide() : true;
        String isRepeatGuideStr = Boolean.TRUE.equals(isRepeatGuide) ? "적용" : "미적용";

        Boolean isLowPressure = (voice != null) ? voice.getIsLowPressure() : true;
        String isLowPressureStr = Boolean.TRUE.equals(isLowPressure) ? "적용" : "미적용";

        Boolean isPositiveFeedback = (voice != null) ? voice.getIsPositiveFeedback() : true;
        String isPositiveFeedbackStr = Boolean.TRUE.equals(isPositiveFeedback) ? "적용" : "미적용";

        // 4. 플레이스홀더 치환
        String finalPrompt = promptTemplate
                .replace("{patient_status}", patientStatus)
                .replace("{patient_name}", patientNameStr)
                .replace("{personality}", personalityStr)
                .replace("{style}", styleStr)
                .replace("{sentence_length}", sentenceLengthStr)
                .replace("{is_honorific}", isHonorificStr)
                .replace("{is_repeat_guide}", isRepeatGuideStr)
                .replace("{is_low_pressure}", isLowPressureStr)
                .replace("{is_positive_feedback}", isPositiveFeedbackStr)
                .replace("{life_db_context}", lifeDbStr)
                .replace("{time_orientation_instruction}", timeInstructionStr);

        // System 역할 설정
        String systemRole = "당신은 치매 환자를 위한 맞춤형 퀴즈를 출제하는 AI 보조관입니다. 응답은 오직 지정된 JSON 배열로만 출력하세요.";

        // 5. OpenAiClient 호출
        String jsonResponse = openAiClient.generateQuizJson(systemRole, finalPrompt);

        // 6. JSON 응답 파싱 및 정리 (Markdown 백틱 제거)
        try {
            String cleanedJson = jsonResponse.replaceAll("```json", "").replaceAll("```", "").trim();
            return objectMapper.readValue(cleanedJson, new TypeReference<List<GeneratedQuizItemDto>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("AI 퀴즈 생성 응답 파싱 실패: " + e.getMessage(), e);
        }
    }
}
