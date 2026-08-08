package com.eum.hello_lux_quiz.service;

import com.eum.hello_lux_quiz.dto.QuizAnswerResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class QuizScoringService {

    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;

    public QuizScoringService(OpenAiClient openAiClient, ObjectMapper objectMapper) {
        this.openAiClient = openAiClient;
        this.objectMapper = objectMapper;
    }

    // prompt_scoring.txt 파일 불러오기 메서드
    private String loadPromptTemplate() {
        try {
            ClassPathResource resource = new ClassPathResource("prompt_scoring.txt");
            try (InputStream is = resource.getInputStream()) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            throw new RuntimeException("채점 프롬프트 템플릿 파일을 읽는데 실패했습니다.", e);
        }
    }

    public QuizAnswerResponse scoreAnswer(String quizQuestion, String correctAnswer, String userAnswer) {

        // ------------------------------------------------------------------
        // 💡 1. 제출 답안과 정답이 완전히 같은 경우 (API 호출 없이 즉시 정답 처리)
        // ------------------------------------------------------------------
        if (userAnswer != null && correctAnswer != null
                && userAnswer.trim().equalsIgnoreCase(correctAnswer.trim())) {
            return new QuizAnswerResponse(true, "정확하게 잘 맞춰주셨어요! 아주 훌륭합니다.");
        }

        // ------------------------------------------------------------------
        // 💡 2. '모르겠습니다' 등의 미답변/모름 처리 (API 호출 없이 즉시 오답 및 격려 처리)
        // ------------------------------------------------------------------
        if (userAnswer == null || userAnswer.isBlank()
                || userAnswer.contains("모르겠습니다") || userAnswer.contains("모르겠") || userAnswer.contains("모름")) {
            return new QuizAnswerResponse(false, "괜찮아요, 기억이 나지 않으실 수도 있어요. 다음 문제도 함께 천천히 풀어봐요!");
        }

        // ------------------------------------------------------------------
        // 💡 3. 그 외 입력값(유사어, 오타 등)인 경우 LLM 채점 API 호출
        // ------------------------------------------------------------------
        String promptTemplate = loadPromptTemplate();
        String userPrompt = promptTemplate
                .replace("{quiz_question}", quizQuestion != null ? quizQuestion : "")
                .replace("{correct_answer}", correctAnswer != null ? correctAnswer : "")
                .replace("{user_answer}", userAnswer != null ? userAnswer : "");

        String systemRole = "당신은 치매 환자의 답변을 채점하는 AI 채점관입니다. 부연 설명 없이 오직 JSON으로만 응답하세요.";

        try {
            // LLM 호출 및 파싱
            String jsonResponse = openAiClient.generateQuizJson(systemRole, userPrompt);
            String cleanedJson = jsonResponse.replaceAll("```json", "").replaceAll("```", "").trim();

            JsonNode root = objectMapper.readTree(cleanedJson);
            boolean isCorrect = root.get("is_correct").asBoolean();
            String feedback = root.get("feedback").asText();

            return new QuizAnswerResponse(isCorrect, feedback);

        } catch (Exception e) {
            // 예외 발생 원인을 콘솔 로그에 상세히 출력하도록 e.printStackTrace() 추가
            e.printStackTrace();
            return new QuizAnswerResponse(false, "답변을 확인하는 중 오류가 발생했습니다.");
        }
    }
}
