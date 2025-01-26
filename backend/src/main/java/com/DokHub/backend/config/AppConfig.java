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
        System.out.println("YouTube API Key: " + System.getenv("YOUTUBE_API_KEY"));
        System.out.println("MySQL Password: " + System.getenv("MYSQL_ROOT"));
    }
}
