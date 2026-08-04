package com.eum.hello_lux_quiz.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.eum.hello_lux_quiz.dto.ChatCompletionRequest;
import com.eum.hello_lux_quiz.dto.ChatCompletionResponse;

@Component
public class OpenAiClient {

    private final RestClient restClient;

    public OpenAiClient(
            @Value("${openai.api.key}") String apiKey,
            @Value("${openai.api.url:https://generativelanguage.googleapis.com/v1beta/openai/}") String baseUrl
    ) {
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
                /*  .responseFormat(Map.of("type", "json_object")) // JSON 전용 응답 강제 (dto 지원 시 적용) */
                .messages(List.of(
                        new ChatCompletionRequest.ChatMessage("system", systemPrompt),
                        new ChatCompletionRequest.ChatMessage("user", userPrompt)
                ))
                .build();

        ChatCompletionResponse response = restClient.post()
                .uri("chat/completions")
                .body(request)
                .retrieve()
                .body(ChatCompletionResponse.class);

        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            var choice = response.getChoices().get(0);
            if (choice != null && choice.getMessage() != null) {
                return choice.getMessage().getContent();
            }
        }

        throw new RuntimeException("LLM 응답을 받아오지 못했거나 응답 형식이 올바르지 않습니다.");
    }

    // 기본 모델명을 안정적인 모델명으로 지정 (예: gemini-3.6-flash)
    public String generateQuizJson(String systemPrompt, String userPrompt) {
        return generateQuizJson(systemPrompt, userPrompt, "gemini-3.6-flash");
    }

    public String callSimpleGpt(String promptText) {
        String systemPrompt = "너는 친절하고 따뜻한 회상 치료 보조 AI이다. 주어진 조건에 맞게 딱 1문장의 힌트만 생성해라.";
        return generateQuizJson(systemPrompt, promptText, "gemini-3.6-flash");
    }
}
