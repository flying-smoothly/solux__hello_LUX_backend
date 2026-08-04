package com.eum.hello_lux_quiz.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class LifeDbService {

    private final LifeDbRepository lifeDbRepository;
    private final DetailRepository detailRepository;
    private final S3Client s3Client; // Cloudflare R2 연동용 S3 Client 주입
    private final ObjectMapper objectMapper; //  JSON 변환용 ObjectMapper 추가

    @Value("${cloudflare.r2.bucket-name}")
    private String bucketName;

    @Value("${cloudflare.r2.public-url}")
    private String publicUrl;

    // 1. 삶의 DB 최초 등록
    @Transactional
    public Integer saveLifeDb(Integer pCode, LifeDbRequestDto request) {
        LocalDate parsedDate = (request.getRecordDate() != null && !request.getRecordDate().isBlank())
                ? LocalDate.parse(request.getRecordDate())
                : LocalDate.now();

        //  List<FamilyDto> -> JSON 문자열 변환
        String familyJson = convertFamilyToJson(request.getFamily());

        LifeDb lifeDb = new LifeDb(
                pCode,
                request.getTitle(),
                parsedDate,
                request.getPlace(),
                request.getLike(),
                request.getJob(),
                request.getHometown(),
                familyJson
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

        //  List<FamilyDto> -> JSON 문자열 변환
        String familyJson = convertFamilyToJson(request.getFamily());

        lifeDb.updateInfo(
                request.getPlace(),
                request.getLike(),
                request.getJob(),
                request.getHometown(),
                familyJson
        );

        return lifeDb.getMemoryId();
    }

    // 5. 세분화 사건 이미지 업로드 (Cloudflare R2 적용)
    @Transactional
    public String uploadImage(Integer pCode, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("업로드할 이미지 파일이 존재하지 않습니다.");
        }

        // 파일명 중복 방지를 위한 UUID 적용 (경로: patients/{pCode}/UUID_originalName)
        String fileName = "patients/" + pCode + "/" + UUID.randomUUID() + "_" + image.getOriginalFilename();

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(image.getContentType())
                    .build();

            // Cloudflare R2에 업로드 실행
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(image.getInputStream(), image.getSize()));

            // 업로드 완료 후 이미지에 접근할 수 있는 URL 리턴
            return publicUrl + "/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Cloudflare R2 이미지 업로드 중 오류가 발생했습니다.", e);
        }
    }

    //  Object/List 객체를 DB 저장용 JSON 문자열로 직렬화하는 헬퍼 메서드
    private String convertFamilyToJson(Object familyObj) {
        if (familyObj == null) {
            return null;
        }
        if (familyObj instanceof String str) {
            return str;
        }
        try {
            return objectMapper.writeValueAsString(familyObj);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("가족 정보 JSON 변환 실패", e);
        }
    }
}
