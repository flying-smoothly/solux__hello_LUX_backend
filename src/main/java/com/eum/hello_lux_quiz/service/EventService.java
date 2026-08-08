package com.eum.hello_lux_quiz.service;

import com.eum.hello_lux_quiz.domain.DetailEvent; // 세분화 엔티티
import com.eum.hello_lux_quiz.domain.Hint;
import com.eum.hello_lux_quiz.repository.DetailRepository;
import com.eum.hello_lux_quiz.repository.HintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final DetailRepository detailRepository;
    private final HintRepository hintRepository;
    private final OpenAiClient openAiClient;

    @Transactional
    public void createEventWithHint(Integer pCode, Integer memoryId, String eventText, String photoUrl, String category) {
        // 💡 1. DetailEvent 수동 생성자 사용 (builder() 제거 및 Integer memoryId 적용)
        DetailEvent event = new DetailEvent(
                memoryId,
                eventText,
                photoUrl,
                category
        );
        DetailEvent savedEvent = detailRepository.save(event);

        // 2. OpenAI 호출하여 회상 힌트 1문장 생성
        String prompt = String.format("사건: %s, 카테고리: %s에 대한 힌트 1문장 생성", eventText, category);
        String generatedHint = openAiClient.callSimpleGpt(prompt);

        // 3. 힌트 DB 저장
        // (Note: Hint 엔티티에 @Builder가 있다면 builder()를 유지해도 되지만, eventId 전달 시 savedEvent.getEventId()가 Integer이므로 타입 호환을 맞춰주어야 합니다.)
        Hint hint = Hint.builder()
                .pCode(pCode != null ? pCode.longValue() : null) // Hint엔티티의 pCode/eventId가 Long 타입이라면 longValue() 변환
                .eventId(savedEvent.getEventId() != null ? savedEvent.getEventId().longValue() : null)
                .setId(1L)
                .hintText(generatedHint)
                .build();

        hintRepository.save(hint);
    }
}
