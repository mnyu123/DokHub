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

    private static final String BASE_CHZZK_API_URL = "https://openapi.chzzk.naver.com/open/v1/lives";
    private static final String TARGET_CHANNEL_ID   = "4de764d9dad3b25602284be6db3ac647";
    // 4de764d9dad3b25602284be6db3ac647 , b68af124ae2f1743a1dcbf5e2ab41e0b

    @Value("${chzzk.client.id}")
    private String clientId;

    @Value("${chzzk.client.secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ─── 캐시 설정 ───
    private static final long TTL_MS = 15_000L;      // 15초
    private volatile boolean cachedLive  = false;   // 마지막 판단 결과
    private volatile long    cachedAt     = 0L;      // 마지막 API 호출 시각
    // ─────────────────

    public boolean isChannelLive() {
        long now = System.currentTimeMillis();

        // (1) 이전에 ON 이었고, TTL 안 지났으면 바로 ON 반환
        if (cachedLive && (now - cachedAt) < TTL_MS) {
            log.info("[DOKHUB]: 캐시 HIT → 이전 ON 상태 유지 (남은 {}ms)",
                    TTL_MS - (now - cachedAt));
            return true;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Client-Id", clientId);
            headers.add("Client-Secret", clientSecret);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            String nextToken = null;
            do {
                String apiUrl = BASE_CHZZK_API_URL + "?size=20" +
                        (nextToken != null ? "&next=" + nextToken : "");
                log.info("[DOKHUB]: 치지직 API 호출 → {}", apiUrl);

                ResponseEntity<String> resp;
                try {
                    resp = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
                } catch (HttpClientErrorException e) {
                    if (e.getStatusCode() == HttpStatus.BAD_REQUEST &&
                            e.getResponseBodyAsString().contains("잘못된 next 값입니다")) {
                        log.info("[DOKHUB]: 더 이상 페이지 없음");
                        break;
                    }
                    throw e;
                }

                JsonNode root    = objectMapper.readTree(resp.getBody());
                JsonNode content = root.path("content");
                JsonNode data    = content.path("data");
                if (data.isArray()) {
                    for (JsonNode item : data) {
                        String cid = item.path("channelId").asText();
                        if (TARGET_CHANNEL_ID.equals(cid)) {
                            // ON 발견 → 캐시 갱신 후 바로 true 반환
                            cachedLive = true;
                            cachedAt   = now;
                            log.info("[DOKHUB]: 채널 {} LIVE 확인 → ON", TARGET_CHANNEL_ID);
                            return true;
                        }
                    }
                }

                // 다음 페이지 토큰
                nextToken = content.path("page").path("next").asText(null);
                if (nextToken == null || nextToken.isEmpty()) break;

            } while (true);

            // (2) 루프 끝까지 못 찾으면 OFF
            if (cachedLive) {
                // 이전 ON 이었는데 OFF 감지 → TTL 동안 ON 유지
                log.warn("[DOKHUB]: OFF 감지됐으나 TTL({}ms) 내, ON 유지", TTL_MS);
                return true;
            }
            // 완전 OFF → 캐시 갱신
            cachedLive = false;
            cachedAt   = now;
            log.info("[DOKHUB]: 채널 {} OFF 확인", TARGET_CHANNEL_ID);

        } catch (Exception ex) {
            log.error("[DOKHUB]: 방송 상태 조회 중 오류 – 직전 캐시값 유지", ex);
        }

        return false;
    }
}
