package com.eum.hello_lux_quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatCompletionRequest {

    private String model;
    private List<ChatMessage> messages;
    private double temperature;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatMessage {

        private String role;    // "system", "user"
        private String content;
    }
}
