package com.eum.hello_lux_quiz.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eum.hello_lux_quiz.domain.DetailEvent;
import com.eum.hello_lux_quiz.domain.LifeDb;
import com.eum.hello_lux_quiz.dto.LifeDbEventRequestDto;
import com.eum.hello_lux_quiz.dto.LifeDbRequestDto;
import com.eum.hello_lux_quiz.dto.LifeDbResponseDto;
import com.eum.hello_lux_quiz.dto.LifeDbUpdateRequestDto;
import com.eum.hello_lux_quiz.repository.DetailRepository;
import com.eum.hello_lux_quiz.repository.LifeDbRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LifeDbService {

    private final LifeDbRepository lifeDbRepository;
    private final DetailRepository detailRepository; // 💡 DetailRepository 적용

    // 1. 삶의 DB 최초 등록
    @Transactional
    public Integer saveLifeDb(Integer pCode, LifeDbRequestDto request) {
        LocalDate parsedDate = (request.getRecordDate() != null && !request.getRecordDate().isBlank())
                ? LocalDate.parse(request.getRecordDate())
                : LocalDate.now();

        // 수동 생성자로 엔티티 생성
        LifeDb lifeDb = new LifeDb(
                pCode,
                request.getTitle(),
                parsedDate,
                request.getPlace(),
                request.getLike(),
                request.getJob(),
                request.getHometown(),
                request.getFamily()
        );

        LifeDb saved = lifeDbRepository.save(lifeDb);

        if (request.getEvent() != null && !request.getEvent().isBlank()) {
            DetailEvent detailEvent = new DetailEvent(
                    saved.getMemoryId(),
                    request.getEvent(),
                    request.getPhotoUrl(),
                    request.getCategory()
            );
            detailRepository.save(detailEvent);
        }
        return saved.getMemoryId();
    }

    // 2. 삶의 DB 단건 조회
    @Transactional(readOnly = true)
    public LifeDbResponseDto getLifeDb(Integer pCode, Integer memoryId) {
        LifeDb lifeDb = lifeDbRepository.findById(memoryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 삶의 DB를 찾을 수 없습니다. id=" + memoryId));

        // 1. 해당 memoryId에 해당하는 세분화 사건 목록 가져오기
        List<DetailEvent> events = detailRepository.findByMemoryId(memoryId);

        // 2. lifeDb와 events를 함께 전달 (인자 2개)
        return new LifeDbResponseDto(lifeDb, events); // ✅ 해결!
    }

    // 3. 환자 삶의 DB 사건 추가 (세분화 테이블 저장)
    @Transactional
    public Integer addEventToLifeDb(Integer pCode, Integer memoryId, LifeDbEventRequestDto request) {
        if (!lifeDbRepository.existsById(memoryId)) {
            throw new IllegalArgumentException("존재하지 않는 memory_id 입니다: " + memoryId);
        }

        DetailEvent detailEvent = new DetailEvent(
                memoryId,
                request.getEvent(),
                request.getPhotoUrl(),
                request.getCategory()
        );

        DetailEvent savedEvent = detailRepository.save(detailEvent);
        return savedEvent.getEventId();
    }

    // 4. 삶의 DB 수정 (삶의DB 테이블 기본 정보 수정)
    @Transactional
    public Integer updateLifeDb(Integer pCode, LifeDbUpdateRequestDto request) {
        LifeDb lifeDb = lifeDbRepository.findById(request.getMemoryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 삶의 DB를 찾을 수 없습니다. id=" + request.getMemoryId()));

        lifeDb.updateInfo(
                request.getPlace(),
                request.getLike(),
                request.getJob(),
                request.getHometown(),
                request.getFamily()
        );

        return lifeDb.getMemoryId();
    }
}

