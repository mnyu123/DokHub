package com.DokHub.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ChzzkLiveService {

    private final RestTemplate restTemplate;
    private final String clientId;
    private final String clientSecret;
    private final String baseUrl = "https://openapi.chzzk.naver.com";

    // 독케익 채널 ID (치지직 API에서 방송키로 사용)
    private final String channelId = "b68af124ae2f1743a1dcbf5e2ab41e0b";

    public ChzzkLiveService(RestTemplate restTemplate,
                            @Value("${chzzk.client.id}") String clientId,
                            @Value("${chzzk.client.secret}") String clientSecret) {
        this.restTemplate = restTemplate;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    /**
     * 라이브 방송 설정 조회 (GET /open/v1/lives/setting)
     */
    public Map<String, Object> getLiveSettings() {
        String url = baseUrl + "/open/v1/lives/setting?channelId=" + channelId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Client-Id", clientId);
        headers.set("Client-Secret", clientSecret);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        return response.getBody();
    }

    /**
     * 라이브 방송 설정 변경 (PATCH /open/v1/lives/setting)
     *
     * @param liveOn true면 방송 켜기(라이브 on), false면 방송 끄기(오프)로 설정
     */
    public Map<String, Object> updateLiveSettings(boolean liveOn) {
        String url = baseUrl + "/open/v1/lives/setting?channelId=" + channelId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Client-Id", clientId);
        headers.set("Client-Secret", clientSecret);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        if (liveOn) {
            // 라이브 on: 방송 제목과 설정값을 지정
            requestBody.put("defaultLiveTitle", "독케익 방송 중");
            requestBody.put("categoryType", "GAME");  // 예시 값
            requestBody.put("categoryId", "game123");   // 예시 값
            requestBody.put("tags", Arrays.asList("독케익", "라이브"));
        } else {
            // 라이브 off: 방송 제목을 '방송 종료'로 업데이트 (빈 값은 허용되지 않으므로)
            requestBody.put("defaultLiveTitle", "방송 종료");
            // 필요에 따라 태그나 카테고리도 초기화 가능
            requestBody.put("tags", new ArrayList<>());
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PATCH, entity, Map.class);
        return response.getBody();
    }
}
