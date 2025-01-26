package com.DokHub.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로 허용 (/api/** 등)
                .allowedOrigins(
                        "http://localhost:3000",   // ← 개발용 프론트엔드 (Vue dev server)
                        "http://127.0.0.1:3000",  // ← 혹은 127.0.0.1 if needed
                        "https://dokhub-love-doksaem.netlify.app",
                        "http://52.79.242.58", // AWS 주소 추가
                        "https://www.googleapis.com"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
    }
}

