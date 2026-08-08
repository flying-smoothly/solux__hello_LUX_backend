package com.eum.hello_lux_quiz.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.eum.hello_lux_quiz.dto.ChatCompletionRequest;
import com.eum.hello_lux_quiz.dto.ChatCompletionResponse;
import com.fasterxml.jackson.databind.ObjectMapper; // 👈 추가

import lombok.extern.slf4j.Slf4j; // 👈 추가

@Slf4j // 👈 추가 (log.info 사용)
@Component
public class OpenAiClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper; // 👈 추가 (String -> DTO 변환용)

    public OpenAiClient(
            @Value("${openai.api.key}") String apiKey,
            @Value("${openai.api.url:https://generativelanguage.googleapis.com/v1beta/openai/}") String baseUrl,
            ObjectMapper objectMapper // 👈 Spring이 자동으로 주입해 줍니다.
    ) {
        this.objectMapper = objectMapper;

        System.out.println("=== [OpenAiClient] Loaded API Key: "
                + (apiKey != null && apiKey.length() > 5 ? apiKey.substring(0, 5) + "*****" : "NULL"));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl.endsWith("/") ? baseUrl : baseUrl + "/")
                .defaultHeader("Authorization", "Bearer " + (apiKey != null ? apiKey.trim() : ""))
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * OpenAI 호환 Chat Completion 호출
     */
    public String generateQuizJson(String systemPrompt, String userPrompt, String modelName) {
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(modelName)
                .temperature(0.7)
                .messages(List.of(
                        new ChatCompletionRequest.ChatMessage("system", systemPrompt),
                        new ChatCompletionRequest.ChatMessage("user", userPrompt)
                ))
                .build();

        // 1. DTO가 아닌 Raw String으로 응답 받기
        String rawResponseBody = restClient.post()
                .uri("chat/completions")
                .body(request)
                .retrieve()
                .body(String.class);

        // 2. 🚀 터미널에 LLM 응답 원문(Raw Response) 로그 출력!
        log.info("==================== [LLM RAW RESPONSE START] ====================");
        log.info("\n{}", rawResponseBody);
        log.info("==================== [LLM RAW RESPONSE END] ====================");

        // 3. Raw String을 DTO로 파싱
        ChatCompletionResponse response;
        try {
            response = objectMapper.readValue(rawResponseBody, ChatCompletionResponse.class);
        } catch (Exception e) {
            log.error("❌ LLM Raw Response -> ChatCompletionResponse DTO 파싱 실패!", e);
            throw new RuntimeException("LLM 응답 JSON 파싱 실패", e);
        }

        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            var choice = response.getChoices().get(0);
            if (choice != null && choice.getMessage() != null) {
                String content = choice.getMessage().getContent();

                // 4. 🚀 choice.message.content 내부의 퀴즈 JSON 원문도 따로 확인하고 싶다면 추가 로그
                log.info("🔍 [QUIZ CONTENT ONLY]: \n{}", content);

                return content;
            }
        }

        throw new RuntimeException("LLM 응답을 받아오지 못했거나 응답 형식이 올바르지 않습니다.");
    }

    // 기본 모델명을 지정
    public String generateQuizJson(String systemPrompt, String userPrompt) {
        return generateQuizJson(systemPrompt, userPrompt, "gemini-3.6-flash");
    }

    public String callSimpleGpt(String promptText) {
        String systemPrompt = "너는 친절하고 따뜻한 회상 치료 보조 AI이다. 주어진 조건에 맞게 딱 1문장의 힌트만 생성해라.";
        return generateQuizJson(systemPrompt, promptText, "gemini-3.6-flash");
    }
}
