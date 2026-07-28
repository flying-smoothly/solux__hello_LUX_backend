package com.eum.hello_lux_quiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// authentication 브랜치의 BaseTimeEntity(@CreatedDate/@LastModifiedDate) 감사 기능 활성화
// (기존 SoluxWebApplication 의 @EnableJpaAuditing 를 단일 메인 클래스로 통합)
@EnableJpaAuditing
@SpringBootApplication
public class HelloLuxQuizApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloLuxQuizApplication.class, args);
	}

}
