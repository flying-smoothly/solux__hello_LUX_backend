package com.eum.hello_lux_quiz.service;

import com.eum.hello_lux_quiz.domain.PatientProfile;
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

    // 수동 생성자 주입
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

        // Null 및 기본값 처리
        String patientNameStr = profile.getName() != null ? profile.getName() : "";
        String personalityStr = profile.getPersonality() != null ? profile.getPersonality() : "";
        String styleStr = profile.getStyle() != null ? profile.getStyle() : "";
        String lifeDbStr = lifeDbContext != null ? lifeDbContext : "";
        String timeInstructionStr = timeOrientationInstruction != null ? timeOrientationInstruction : "";

        // 2. 새로 추가된 개인화 설정값 읽기 및 가공
        String sentenceLengthStr = profile.getSentenceLength() != null ? profile.getSentenceLength() : "보통";
        String isHonorificStr = (profile.getIsHonorific() != null && profile.getIsHonorific()) ? "존댓말 사용" : "편안한 어투";
        String isRepeatGuideStr = (profile.getIsRepeatGuide() != null && profile.getIsRepeatGuide()) ? "적용" : "미적용";
        String isLowPressureStr = (profile.getIsLowPressure() != null && profile.getIsLowPressure()) ? "적용" : "미적용";
        String isPositiveFeedbackStr = (profile.getIsPositiveFeedback() != null && profile.getIsPositiveFeedback()) ? "적용" : "미적용";

        // 3. 플레이스홀더 치환 (개인화 설정 항목 추가)
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

        // System 역할과 User 요청을 하나로 전달하거나 분리하여 전달
        String systemRole = "당신은 치매 환자를 위한 맞춤형 퀴즈를 출제하는 AI 보조관입니다. 응답은 오직 지정된 JSON 배열로만 출력하세요.";

        // 4. OpenAiClient 호출
        String jsonResponse = openAiClient.generateQuizJson(systemRole, finalPrompt);

        // 5. JSON 응답 파싱
        try {
            String cleanedJson = jsonResponse.replaceAll("```json", "").replaceAll("```", "").trim();
            return objectMapper.readValue(cleanedJson, new TypeReference<List<GeneratedQuizItemDto>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("AI 퀴즈 생성 응답 파싱 실패: " + e.getMessage(), e);
        }
    }
}
