package com.DokHub.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class ChzzkLiveService {

    private static final String BASE_CHZZK_API_URL = "https://openapi.chzzk.naver.com/open/v1/lives";
    private static final String TARGET_CHANNEL_ID = "b68af124ae2f1743a1dcbf5e2ab41e0b";
    private static final long CACHE_MILLIS = 45_000L;

    @Value("${chzzk.client.id:}")
    private String clientId;
    @Value("${chzzk.client.secret:}")
    private String clientSecret;
    @Value("${chzzk.live.enabled:true}")
    private boolean liveEnabled;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ChzzkChatService chzzkChatService;
    private final Object cacheLock = new Object();

    private volatile boolean cachedLive;
    private volatile long cachedAt;

    public ChzzkLiveService(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            ChzzkChatService chzzkChatService) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.chzzkChatService = chzzkChatService;
    }

    public boolean isChannelLive() {
        long now = System.currentTimeMillis();
        if (now - cachedAt < CACHE_MILLIS) {
            return cachedLive;
        }

        synchronized (cacheLock) {
            now = System.currentTimeMillis();
            if (now - cachedAt < CACHE_MILLIS) {
                return cachedLive;
            }
            cachedLive = fetchChannelLive();
            cachedAt = now;
            chzzkChatService.ensureChatConnection(cachedLive);
            return cachedLive;
        }
    }

    private boolean fetchChannelLive() {
        if (!liveEnabled || isBlank(clientId) || isBlank(clientSecret)) {
            return false;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Client-Id", clientId);
            headers.add("Client-Secret", clientSecret);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            String nextToken = null;
            for (int pageCount = 1; pageCount <= 100; pageCount++) {
                StringBuilder url = new StringBuilder(BASE_CHZZK_API_URL).append("?size=20");
                if (!isBlank(nextToken)) {
                    url.append("&next=").append(URLEncoder.encode(nextToken, StandardCharsets.UTF_8));
                }

                ResponseEntity<String> response;
                try {
                    response = restTemplate.exchange(URI.create(url.toString()), HttpMethod.GET, entity, String.class);
                } catch (HttpClientErrorException exception) {
                    if (exception.getStatusCode() == HttpStatus.BAD_REQUEST
                            || exception.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                        log.warn("[DOKHUB] Chzzk 라이브 조회 중단(status={})", exception.getStatusCode());
                        return false;
                    }
                    throw exception;
                }

                JsonNode content = objectMapper.readTree(response.getBody()).path("content");
                JsonNode data = content.path("data");
                if (data.isArray()) {
                    for (JsonNode item : data) {
                        if (TARGET_CHANNEL_ID.equals(item.path("channelId").asText())) {
                            return true;
                        }
                    }
                }

                nextToken = content.path("page").path("next").asText(null);
                if (isBlank(nextToken)) {
                    return false;
                }
            }
        } catch (Exception exception) {
            log.error("[DOKHUB] Chzzk 방송 상태 조회 실패", exception);
        }
        return false;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
