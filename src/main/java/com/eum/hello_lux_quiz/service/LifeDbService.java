package com.eum.hello_lux_quiz.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
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

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class LifeDbService {

    private final LifeDbRepository lifeDbRepository;
    private final DetailRepository detailRepository;

    // R2 업로드를 위한 S3Client 및 설정값 주입
    private final S3Client s3Client;

    @Value("${cloudflare.r2.bucket-name}")
    private String bucketName;

    @Value("${cloudflare.r2.public-url}") // R2 커스텀 도메인 또는 R2.dev 퍼블릭 URL
    private String publicUrl;

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

    // 4. 삶의 DB 수정 (pCode 기반 단건 조회)
    @Transactional
    public Integer updateLifeDb(Integer pCode, LifeDbUpdateRequestDto request) {
        // pCode로 해당 환자의 삶의 DB 단건 조회
        List<LifeDb> lifeDbList = lifeDbRepository.findByPCode(pCode);
        if (lifeDbList.isEmpty()) {
            throw new IllegalArgumentException("해당 환자의 삶의 DB를 찾을 수 없습니다. pCode=" + pCode);
        }

        // 환자당 1개만 존재하는 LifeDb 추출
        LifeDb lifeDb = lifeDbList.get(0);

        lifeDb.updateInfo(
                request.getPlace(),
                request.getLike(),
                request.getJob(),
                request.getHometown(),
                request.getFamily()
        );

        return lifeDb.getMemoryId();
    }

    // 5. 환자 이미지 파일 업로드 (Cloudflare R2)
    @Transactional
    public String uploadPatientImage(Integer pCode, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 비어있습니다.");
        }

        // 원본 파일명(한글/공백 포함 가능)을 그대로 key 로 쓰면 URL 인코딩 불일치로 이미지가 깨진다.
        // 확장자만 남기고 UUID 로 대체한다.
        String storeFileName = "patients/" + pCode + "/" + UUID.randomUUID() + extractExtension(file.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(storeFileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // 저장된 이미지의 접근 가능한 URL 반환
            return buildPublicUrl(storeFileName);

        } catch (IOException e) {
            throw new RuntimeException("R2 파일 업로드 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 원본 파일명에서 URL 에 안전한 확장자만 추출한다. (예: "가족 사진.JPG" -> ".jpg")
     * 확장자가 없거나 이상하면 빈 문자열을 반환한다.
     */
    private String extractExtension(String originalFilename) {
        if (originalFilename == null) {
            return "";
        }
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == originalFilename.length() - 1) {
            return "";
        }
        String extension = originalFilename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
        if (!extension.matches("[a-z0-9]{1,10}")) {
            return "";
        }
        return "." + extension;
    }

    /**
     * 업로드된 오브젝트의 공개 URL 조립.
     *
     * <p>R2_PUBLIC_URL 이 비어 있으면 "/patients/1/xxx.jpg" 같은 상대 경로가 만들어지고,
     * 프론트엔드는 이를 자기 origin(예: localhost:5173) 기준으로 해석해 404 가 난다.
     * 사진이 안 뜨는 원인이 되므로, 설정 누락은 업로드 시점에 바로 실패시킨다.
     */
    private String buildPublicUrl(String objectKey) {
        if (publicUrl == null || publicUrl.isBlank()) {
            throw new IllegalStateException(
                    "R2 공개 URL 설정(cloudflare.r2.public-url / 환경변수 R2_PUBLIC_URL)이 비어 있습니다. "
                    + "설정하지 않으면 저장되는 사진 주소가 상대 경로가 되어 퀴즈에서 이미지가 표시되지 않습니다.");
        }

        // 설정값 끝의 '/' 와 key 앞의 '/' 가 겹쳐 '//' 가 되는 것을 방지
        String base = publicUrl.trim();
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        String key = objectKey.startsWith("/") ? objectKey.substring(1) : objectKey;

        return base + "/" + key;
    }
}
