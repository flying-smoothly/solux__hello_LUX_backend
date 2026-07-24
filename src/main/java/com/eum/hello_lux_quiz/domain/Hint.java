package com.eum.hello_lux_quiz.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "힌트")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Hint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hint_id")
    private Long hintId;

    @Column(name = "p_code", nullable = false)
    private Long pCode;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "set_id", nullable = false)
    private Long setId;

    @Column(name = "hint_text", nullable = false, columnDefinition = "TEXT")
    private String hintText;
}
