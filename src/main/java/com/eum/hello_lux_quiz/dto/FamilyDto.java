package com.eum.hello_lux_quiz.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FamilyDto {

    private String name;     // 이름
    private Integer age;     // 나이
    private String relation; // 호칭/관계

    // 수동 생성자
    public FamilyDto(String name, Integer age, String relation) {
        this.name = name;
        this.age = age;
        this.relation = relation;
    }
}
