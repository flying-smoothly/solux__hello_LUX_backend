package com.eum.hello_lux_quiz.service;

import com.eum.hello_lux_quiz.domain.PatientProfile;
import com.eum.hello_lux_quiz.dto.GeneratedQuizItemDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
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
            log.error("❌ 프롬프트 템플릿 파일 읽기 실패!", e);
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

        // Null 처리
        String patientNameStr = profile.getName() != null ? profile.getName() : "";
        String personalityStr = profile.getPersonality() != null ? profile.getPersonality() : "";
        String styleStr = profile.getStyle() != null ? profile.getStyle() : "";
        String lifeDbStr = lifeDbContext != null ? lifeDbContext : "";
        String timeInstructionStr = timeOrientationInstruction != null ? timeOrientationInstruction : "";

        // 2. 플레이스홀더 치환
        String finalPrompt = promptTemplate
                .replace("{patient_status}", patientStatus)
                .replace("{patient_name}", patientNameStr)
                .replace("{personality}", personalityStr)
                .replace("{style}", styleStr)
                .replace("{life_db_context}", lifeDbStr)
                .replace("{time_orientation_instruction}", timeInstructionStr);

        // System 역할 지시문
        String systemRole = "당신은 치매 환자를 위한 맞춤형 퀴즈를 출제하는 AI 보조관입니다. 응답은 오직 지정된 JSON 배열로만 출력하세요.";

        // 3. OpenAiClient 호출
        log.info("===> [QuizGeneratorService] LLM API 호출 시작...");
        String jsonResponse = openAiClient.generateQuizJson(systemRole, finalPrompt);

        log.info("====================================================");
        log.info("====> [LLM Raw Response (원문)]");
        log.info("{}", jsonResponse);
        log.info("====================================================");

        // 4. 안전한 JSON 응답 파싱
        try {
            if (jsonResponse == null || jsonResponse.isBlank()) {
                throw new IllegalArgumentException("LLM 응답 값이 비어있습니다.");
            }

            String cleanedJson = jsonResponse.trim();

            // 백틱(```json ... ```) 제거
            if (cleanedJson.startsWith("```json")) {
                cleanedJson = cleanedJson.substring(7);
            } else if (cleanedJson.startsWith("```")) {
                cleanedJson = cleanedJson.substring(3);
            }
            if (cleanedJson.endsWith("```")) {
                cleanedJson = cleanedJson.substring(0, cleanedJson.length() - 3);
            }

            cleanedJson = cleanedJson.trim();

            // 대괄호 [ ] 구간만 추출 (앞뒤 텍스트 섞임 방지)
            int firstJsonChar = cleanedJson.indexOf("[");
            int lastJsonChar = cleanedJson.lastIndexOf("]");

            if (firstJsonChar != -1 && lastJsonChar != -1 && firstJsonChar < lastJsonChar) {
                cleanedJson = cleanedJson.substring(firstJsonChar, lastJsonChar + 1);
            }

            List<GeneratedQuizItemDto> result = objectMapper.readValue(cleanedJson, new TypeReference<List<GeneratedQuizItemDto>>() {
            });
            log.info("===> [QuizGeneratorService] JSON 파싱 성공! 생성된 문항 수: {}", result.size());
            return result;

        } catch (Exception e) {
            log.error("====================================================");
            log.error("❌ [QuizGeneratorService] AI 퀴즈 응답 JSON 파싱 실패!");
            log.error("====> Raw Response 원문: {}", jsonResponse);
            log.error("====> 예외 메시지: {}", e.getMessage());
            log.error("====> 에러 스택트레이스:", e);
            log.error("====================================================");

            throw new RuntimeException("AI 퀴즈 생성 응답 파싱 실패: " + e.getMessage(), e);
        }
    }
}
