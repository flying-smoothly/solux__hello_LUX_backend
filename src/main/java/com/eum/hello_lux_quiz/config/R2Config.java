package com.eum.hello_lux_quiz.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class R2Config {

    private static final Logger log = LoggerFactory.getLogger(R2Config.class);

    @Value("${cloudflare.r2.account-id:}")
    private String accountId;

    @Value("${cloudflare.r2.access-key-id:}")
    private String accessKeyId;

    @Value("${cloudflare.r2.secret-access-key:}")
    private String secretAccessKey;

    @Bean
    public S3Client s3Client() {
        boolean configured = hasText(accountId) && hasText(accessKeyId) && hasText(secretAccessKey);

        // R2 자격증명은 사진 업로드에만 쓰인다. 값이 없다고 애플리케이션 부팅 전체를 막으면
        // 자격증명이 없는 팀원은 퀴즈/환자 기능조차 로컬에서 띄울 수 없다.
        // 미설정 시에는 익명 자격증명으로 빈만 만들어 두고, 실제 업로드 시점에 실패시킨다.
        AwsCredentialsProvider credentialsProvider;
        if (configured) {
            credentialsProvider = StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKeyId, secretAccessKey));
        } else {
            credentialsProvider = AnonymousCredentialsProvider.create();
            log.warn("Cloudflare R2 자격증명이 설정되지 않았습니다. 사진 업로드는 동작하지 않습니다. "
                    + "R2_ACCOUNT_ID / R2_ACCESS_KEY_ID / R2_SECRET_ACCESS_KEY 환경변수를 설정하세요.");
        }

        // Cloudflare R2 S3 호환 엔드포인트 URI
        String endpoint = String.format("https://%s.r2.cloudflarestorage.com",
                hasText(accountId) ? accountId : "local");

        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(credentialsProvider)
                .region(Region.US_EAST_1) // Cloudflare R2는 Region을 auto 또는 us-east-1로 설정
                .build();
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
