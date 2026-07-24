package com.eum.hello_lux_quiz.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // 👈 1. 이 임포트가 있어야 합니다
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // 👈 2. 이 어노테이션을 클래스 위에 꼭 붙여주세요!
public class GeneratedQuizItemDto {

    @JsonProperty("quiz_num")
    private Integer quizNum;

    private Integer level;

    @JsonProperty("quiz_category")
    private String quizCategory; // text 또는 photo

    @JsonProperty("quiz_comment")
    private String quizComment;

    @JsonProperty("quiz_photo")
    private String quizPhoto;

    private String answer;

    private List<String> hints;

    private List<String> options;

    // Getter들...
    public Integer getQuizNum() {
        return quizNum;
    }

    public Integer getLevel() {
        return level;
    }

    public String getQuizCategory() {
        return quizCategory;
    }

    public String getQuizComment() {
        return quizComment;
    }

    public String getQuizPhoto() {
        return quizPhoto;
    }

    public String getAnswer() {
        return answer;
    }

    public List<String> getHints() {
        return hints;
    }

    public List<String> getOptions() {
        return options;
    }
}
