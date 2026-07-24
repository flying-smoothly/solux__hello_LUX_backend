package com.eum.hello_lux_quiz.dto;

import com.eum.hello_lux_quiz.domain.LifeDb;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LifeDbResponseDto {

    @JsonProperty("memory_id")
    private Integer memoryId;

    @JsonProperty("p_code")
    private Integer pCode;

    private String title;

    @JsonProperty("record_date")
    private String recordDate;

    private String place;

    @JsonProperty("like")
    private String like;

    private String job;
    private String hometown;
    private String family;

    public LifeDbResponseDto(LifeDb entity) {
        this.memoryId = entity.getMemoryId();
        this.pCode = entity.getPCode(); // 💡 getPCode()로 수정 (대문자 P)
        this.title = entity.getTitle();
        this.recordDate = (entity.getRecordDate() != null) ? entity.getRecordDate().toString() : null;
        this.place = entity.getPlace();
        this.like = entity.getLikes();
        this.job = entity.getJob();
        this.hometown = entity.getHometown();
        this.family = entity.getFamily();
    }
}
