package com.eum.hello_lux_quiz.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "세분화")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DetailEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Integer eventId; // 사건들 (PK)

    @Column(name = "memory_id", nullable = false)
    private Integer memoryId; // 메모리코드 (FK)

    @Column(name = "event", nullable = false, columnDefinition = "TEXT")
    private String event; // 주요_사건

    @Column(name = "photo_url", columnDefinition = "TEXT")
    private String photoUrl; // 사진_url (NULL 허용)

    @Column(name = "category", nullable = false, length = 255)
    private String category; // 카테고리 (기쁨, 슬픔 등)

    public DetailEvent(Integer memoryId, String event, String photoUrl, String category) {
        this.memoryId = memoryId;
        this.event = (event != null && !event.isBlank()) ? event : "";
        this.photoUrl = photoUrl;
        this.category = (category != null && !category.isBlank()) ? category : "기타";
    }
}
