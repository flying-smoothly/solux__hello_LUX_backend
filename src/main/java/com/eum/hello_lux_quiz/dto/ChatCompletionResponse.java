package com.eum.hello_lux_quiz.dto;

import java.util.List; // 👈 추가

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // 👈 응답에 정의되지 않은 필드(finish_reason 등)가 와도 무시
public class ChatCompletionResponse {

    private List<Choice> choices;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true) // 👈 Choice 내부에도 추가
    public static class Choice {

        private Message message;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {

        private String role;
        private String content;
    }
}
