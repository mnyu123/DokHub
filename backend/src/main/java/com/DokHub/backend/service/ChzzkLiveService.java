package com.DokHub.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChzzkLiveService {

    // CHZZK 라이브 목록 조회 API 기본 URL
    private static final String BASE_CHZZK_API_URL = "https://openapi.chzzk.naver.com/open/v1/lives";
    // 체크할 대상 채널 ID
    private static final String TARGET_CHANNEL_ID = "b68af124ae2f1743a1dcbf5e2ab41e0b";
    
    // 임시 테스트용 니니아 f00f6d46ecc6d735b96ecf376b9e5212
    // 독케익 채널 아이디 b68af124ae2f1743a1dcbf5e2ab41e0b

    // application.properties에 정의된 값 주입
    @Value("${chzzk.client.id}")
    private String clientId;

    @Value("${chzzk.client.secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ChzzkChatService chzzkChatService; // 채팅 서비스 주입(Spring)

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
            int pageCount = 0; // 무한 루프 방지용 안전장치
            do {
                pageCount++;

                if (pageCount > 100) {
                    log.warn("[DOKHUB]: 너무 많은 페이지 탐색으로 중단 (무한루프 방지로직)");
                    break;
                }

                // 수정된 URL 조합
                StringBuilder urlBuilder = new StringBuilder(BASE_CHZZK_API_URL).append("?size=20");

                if (nextToken != null && !nextToken.isEmpty()) {
                    // Base64 토큰의 '+' 기호가 서버에서 공백으로 변환되는 것을 막기 위해 명시적으로 URLEncoding 처리
                    String encodedNext = URLEncoder.encode(nextToken, StandardCharsets.UTF_8);
                    urlBuilder.append("&next=").append(encodedNext);
                }

                // RestTemplate이 문자열을 받아 이중으로 인코딩하는 것을 방지하기 위해 URI 객체로 바로 생성
                URI apiUrl = URI.create(urlBuilder.toString());

                log.info("[DOKHUB]: 치지직 API 호출 페이지: {} (next: {})", pageCount, nextToken);

                /* 
                * 기존 String 조합으로 url 구성하는 내용
                // URL을 수동으로 구성하여 next 값을 그대로 전달합니다.
                String apiUrl = BASE_CHZZK_API_URL + "?size=20";
                if (nextToken != null && !nextToken.isEmpty()) {
                    apiUrl += "&next=" + nextToken;
                }

                 */

                log.info("[DOKHUB]: 치지직 API 호출 링크: {}", apiUrl);

                ResponseEntity<String> responseEntity;
                try {
                    responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
                } catch (HttpClientErrorException e) {
                    if (e.getStatusCode() == HttpStatus.BAD_REQUEST &&
                            e.getResponseBodyAsString().contains("잘못된 next 값입니다")) {
                        log.info("[DOKHUB]: 더 이상 페이지 없음 (잘못된 next 값)");
                        break;
                    } else if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                        log.warn("[DOKHUB]: API 호출 제한(Rate Limit) 도달. 해당 시점까지만 탐색.");
                        // 너무 잦은 호출로 차단된 경우 안전을 위해 루프 종료
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
                        if (TARGET_CHANNEL_ID.equals(channelId)) {
                            log.info("[DOKHUB]: 타겟 채널 {} 는 현재 라이브중", TARGET_CHANNEL_ID);

                            // 레거시 API가 자동 연결을 지원하지않아
                            // 라이브상태를 보고 채팅소켓 재연결을 확인짓게 한다.
                            chzzkChatService.ensureChatConnection(true);
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

                // 2. API 차단(HTTP 429) 방지를 위해 페이지를 넘길 때마다 약간의 딜레이 부여
                try {
                    Thread.sleep(150); // 150ms 대기
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }

            } while (true);
            log.info("[DOKHUB]: 타겟 채널 {} 방송 안킴", TARGET_CHANNEL_ID);

            // 방송 안켰으면 채팅 소켓 연결시도가 필요없음
            chzzkChatService.ensureChatConnection(false);

        } catch (Exception e) {
            log.error("[DOKHUB]: 방송 상태 불러오는데 에러남", e);
        }
        return false;
    }
}