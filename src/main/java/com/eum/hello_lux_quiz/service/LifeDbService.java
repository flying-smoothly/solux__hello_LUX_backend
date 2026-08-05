package com.eum.hello_lux_quiz.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final DetailRepository detailRepository;

    private final String uploadDir = System.getProperty("user.dir") + "/uploads/";

    // 1. 삶의 DB 최초 등록
    @Transactional
    public Integer saveLifeDb(Integer pCode, LifeDbRequestDto request) {
        LocalDate parsedDate = (request.getRecordDate() != null && !request.getRecordDate().isBlank())
                ? LocalDate.parse(request.getRecordDate())
                : LocalDate.now();

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

        List<DetailEvent> events = detailRepository.findByMemoryId(memoryId);

        return new LifeDbResponseDto(lifeDb, events);
    }

    // 3. 환자 삶의 DB 사건 추가
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

    // 4. 삶의 DB 수정
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

    // 5. 환자 이미지 파일 업로드
    @Transactional
    public String uploadPatientImage(Integer pCode, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 비어있습니다.");
        }

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String storeFileName = UUID.randomUUID().toString() + "_" + originalFilename;
        String fullPath = uploadDir + storeFileName;

        try {
            file.transferTo(new File(fullPath));
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }

        return "/images/" + storeFileName;
    }
}
