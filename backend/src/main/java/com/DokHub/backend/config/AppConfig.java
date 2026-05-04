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
        System.out.println("[YOUTUBE_API_KEY 존재여부]: " + (System.getenv("YOUTUBE_API_KEY") != null));
        System.out.println("[DB_PASSWORD 존재여부]: " + (System.getenv("DB_PASSWORD") != null));
    }
}
