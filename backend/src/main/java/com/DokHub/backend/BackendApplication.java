package com.DokHub.backend;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching // 캐싱 활성화
@EnableScheduling // 스케줄링 활성화(2025.02.16)
public class BackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@PostConstruct
	public void testEnvVariables() {
		// Dotenv 라이브러리를 통해 환경 변수 확인
		Dotenv dotenv = Dotenv.configure()
				.directory("src/main/resources")
				.load();
		System.out.println("MYSQL_ROOT (Dotenv): " + dotenv.get("MYSQL_ROOT"));
	}
}
