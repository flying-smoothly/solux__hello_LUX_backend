package com.eum.hello_lux_quiz.service;

import java.util.List;

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
        // 주입된 키 정상 유무 체크용 로그 (앞 5자리만 출력)
        System.out.println("=== [OpenAiClient] Loaded API Key: "
                + (apiKey != null && apiKey.length() > 5 ? apiKey.substring(0, 5) + "*****" : "NULL"));

        // baseUrl 끝에 '/'가 붙어있으므로 .uri("chat/completions")와 결합 시 깔끔한 URL이 완성됩니다.
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl.endsWith("/") ? baseUrl : baseUrl + "/")
                .defaultHeader("Authorization", "Bearer " + (apiKey != null ? apiKey.trim() : ""))
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * FactChat Gateway OpenAI 호환 Chat Completion 기본 호출
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

        ChatCompletionResponse response = restClient.post()
                .uri("chat/completions") // baseUrl 뒤에 깔끔하게 연결
                .body(request)
                .retrieve()
                .body(ChatCompletionResponse.class);

        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            return response.getChoices().get(0).getMessage().getContent();
        }

        throw new RuntimeException("LLM 응답을 받아오지 못했습니다.");
    }

    public String generateQuizJson(String systemPrompt, String userPrompt) {
        return generateQuizJson(systemPrompt, userPrompt, "gemini-3.6-flash");
    }

    public String callSimpleGpt(String promptText) {
        String systemPrompt = "너는 친절하고 따뜻한 회상 치료 보조 AI이다. 주어진 조건에 맞게 딱 1문장의 힌트만 생성해라.";
        return generateQuizJson(systemPrompt, promptText, "gemini-3.6-flash");
    }
}
