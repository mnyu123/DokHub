package com.DokHub.backend.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @PostConstruct
    public void testEnvVariables() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()   // ← 핵심
                .load();

        System.out.println("YouTube API Key: " + System.getenv("YOUTUBE_API_KEY"));
        System.out.println("MySQL Password: " + System.getenv("MYSQL_ROOT"));
    }
}
