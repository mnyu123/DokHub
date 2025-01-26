package com.DokHub.backend.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvLoader {
    // Dotenv 객체를 전역적으로 초기화
    private static final Dotenv dotenv = Dotenv.configure()
            .directory("src/main/resources") // .env 파일 경로 명시
            .load();

    // 환경 변수 값을 반환하는 메서드
    public static String get(String key) {
        return dotenv.get(key);
    }

    // 테스트용 메인 메서드
    public static void main(String[] args) {
        System.out.println("YouTube API Key: " + get("YOUTUBE_API_KEY"));
        System.out.println("MySQL Password: " + get("MYSQL_ROOT"));
    }
}
