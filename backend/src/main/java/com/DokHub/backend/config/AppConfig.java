package com.DokHub.backend.config;

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
        System.out.println("[유튜브 키 환경변수 안씀]: " + System.getenv("YOUTUBE_API_KEY"));
        System.out.println("[DB비번 환경변수 안씀]: " + System.getenv("MYSQL_ROOT"));
    }
}
