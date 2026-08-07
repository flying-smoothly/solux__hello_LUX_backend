package com.eum.hello_lux_quiz.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "삶의db")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LifeDb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memory_id")
    private Integer memoryId; // 메모리코드 (PK)

    @Column(name = "p_code", nullable = false)
    private Integer pCode; // 환자코드 (FK)

    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title; // 제목

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate; // 등록일

    @Column(name = "place", nullable = false, columnDefinition = "TEXT")
    private String place; // 자주가는_장소

    @Column(name = "likes", nullable = false, columnDefinition = "TEXT")
    private String likes; // 좋아하는것

    @Column(name = "job", nullable = false, columnDefinition = "TEXT")
    private String job; // 직업

    @Column(name = "hometown", nullable = false, length = 255)
    private String hometown; // 고향

    @Column(name = "family", nullable = false, columnDefinition = "TEXT")
    private String family; // 가족

    public LifeDb(Integer pCode, String title, LocalDate recordDate, String place, String likes, String job, String hometown, String family) {
        this.pCode = pCode;
        this.title = (title != null && !title.isBlank()) ? title : "소중한 기억";
        this.recordDate = (recordDate != null) ? recordDate : LocalDate.now();
        this.place = (place != null) ? place : "";
        this.likes = (likes != null) ? likes : "";
        this.job = (job != null) ? job : "";
        this.hometown = (hometown != null) ? hometown : "";
        this.family = (family != null) ? family : "";
    }

    public void updateInfo(String place, String likes, String job, String hometown, String family) {
        if (place != null) {
            this.place = place;
        }
        if (likes != null) {
            this.likes = likes;
        }
        if (job != null) {
            this.job = job;
        }
        if (hometown != null) {
            this.hometown = hometown;
        }
        if (family != null) {
            this.family = family;
        }
    }
}
