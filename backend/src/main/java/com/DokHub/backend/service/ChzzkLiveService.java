package com.DokHub.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ChzzkLiveService {

    // CHZZK 라이브 목록 조회 API 기본 URL
    private static final String BASE_CHZZK_API_URL = "https://openapi.chzzk.naver.com/open/v1/lives";
    // 체크할 대상 채널 ID
    private static final String TARGET_CHANNEL_ID = "b68af124ae2f1743a1dcbf5e2ab41e0b";

    // 테스트용 방송 체크 채널
    private static final String TARGET_CHANNEL_TEST_ID = "17aa057a8248b53affe30512a91481f5";

    // application.properties에 정의된 값 주입
    @Value("${chzzk.client.id}")
    private String clientId;

    @Value("${chzzk.client.secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * CHZZK API를 호출하여 전체 라이브 목록을 순회하면서
     * 대상 채널이 현재 방송 중인지 체크합니다.
     *
     * @return 대상 채널이 라이브이면 true, 아니면 false
     */
    public boolean isChannelLive() {
        try {
            // 요청 헤더에 Client 인증 정보 추가
            HttpHeaders headers = new HttpHeaders();
            headers.add("Client-Id", clientId);
            headers.add("Client-Secret", clientSecret);
            // 방송 조회는 토큰 사용 안함
            HttpEntity<?> entity = new HttpEntity<>(headers);

            String nextToken = null;
            do {
                // URL을 수동으로 구성하여 next 값을 그대로 전달합니다.
                String apiUrl = BASE_CHZZK_API_URL + "?size=20";
                if (nextToken != null && !nextToken.isEmpty()) {
                    apiUrl += "&next=" + nextToken;
                }
                log.info("[DOKHUB]: 치지직 API 호출 링크: {}", apiUrl);

                ResponseEntity<String> responseEntity;
                try {
                    responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
                } catch (HttpClientErrorException e) {
                    // 400 에러 중 "잘못된 next 값입니다." 메시지가 포함되면 더 이상 페이지가 없다고 판단
                    if (e.getStatusCode() == HttpStatus.BAD_REQUEST &&
                            e.getResponseBodyAsString().contains("잘못된 next 값입니다")) {
                        log.info("[DOKHUB]: 더 이상 페이지 없음");
                        break;
                    } else {
                        throw e;
                    }
                }

                String response = responseEntity.getBody();
                log.info("[DOKHUB]: 치지직 API 호출 응답 받음");

                // 응답 JSON 파싱
                JsonNode root = objectMapper.readTree(response);
                JsonNode content = root.path("content");
                JsonNode data = content.path("data");
                if (data.isArray()) {
                    for (JsonNode item : data) {
                        String channelId = item.path("channelId").asText();
                        // 2025-05-04 서버에 로그가 너무많아서 주석
                        // log.info("[DOKHUB]: 채널 ID 체크: {}", channelId);
                        if (TARGET_CHANNEL_TEST_ID.equals(channelId)) {
                            log.info("[DOKHUB]: 타겟 채널 {} 는 현재 라이브중", TARGET_CHANNEL_ID);
                            return true;
                        }
                    }
                }
                // 다음 페이지의 next 값 확인 (API에서 제공한 값을 그대로 사용)
                JsonNode page = content.path("page");
                nextToken = page.path("next").asText(null);
                if (nextToken == null || nextToken.isEmpty()) {
                    log.info("[DOKHUB]: 더 이상 페이지 없음");
                    break;
                }
            } while (true);
            log.info("[DOKHUB]: 타겟 채널 {} 방송 안킴", TARGET_CHANNEL_ID);
        } catch (Exception e) {
            log.error("[DOKHUB]: 방송 상태 불러오는데 에러남", e);
        }
        return false;
    }
}