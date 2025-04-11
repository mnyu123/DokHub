package com.DokHub.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.socket.client.IO;
import io.socket.client.Socket;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class ChzzkChatService {

    @Value("${chzzk.base-url}")
    private String baseUrl;                  // ex) https://api.chzzk.naver.com

    @Value("${chzzk.access-token}")
    private String accessToken;              // Bearer {token}

    @Value("${chzzk.channel-id}")
    private String channelId;                // 수신하려는 채널(독케익)

    @Value("${chzzk.target-user-id}")
    private String targetUserChannelId;      // 필터 대상 유저(일단 나)

    @Value("${chzzk.client-id}")     // 치지직 인증1
    private String clientId;

    @Value("${chzzk.client-secret}") // 치지직 인증2
    private String clientSecret;


    private final ObjectMapper om = new ObjectMapper();
    private final RestTemplate rest = new RestTemplate();

    /**
     * 최근 채팅 500개만 유지
     */
    @Getter
    private final List<String> chatHistory = new CopyOnWriteArrayList<>();

    private Socket socket;

    /* --------------------------------------------------------------------- */
    /* 1) 애플리케이션 기동 시 세션 만들고 소켓 연결                        */
    /* --------------------------------------------------------------------- */
    @PostConstruct
    @Async        // 별도 스레드에서 비동기로 실행
    public void start() {
        try {
            /* 1‑1. 세션 URL 요청 (유저 인증) */
            String sessionUrl = fetchSessionUrl();
            log.info("[CHZZK] 세션 URL 수신: {}", sessionUrl);

            /* 1‑2. Socket.IO 연결 */
            IO.Options opt = new IO.Options();
            opt.reconnection = false;              // 자동 재연결 비활성
            opt.forceNew = true;                // 항상 새 연결
            opt.timeout = 3_000;               // 연결 타임아웃(ms)
            opt.transports = new String[]{"websocket"};  // WebSocket 전용

            socket = IO.socket(sessionUrl, opt);

            socket.on(Socket.EVENT_CONNECT, args ->
                    log.info("[CHZZK] 소켓 연결 완료"));

            socket.on(Socket.EVENT_CONNECT_ERROR, args ->
                    log.error("[CHZZK] CONNECT_ERROR : {}", Arrays.toString(args)));

            socket.on(Socket.EVENT_DISCONNECT, args ->
                    log.warn("[CHZZK] DISCONNECT : {}", Arrays.toString(args)));

            /* 시스템 이벤트 수신 (connected / subscribed 등) */
            socket.on("SYSTEM", args -> {
                JsonNode node = toJson(args[0]);
                handleSystemEvent(node);
            });

            /* 채팅 이벤트 수신 */
            socket.on("CHAT", args -> {
                JsonNode node = toJson(args[0]);
                handleChatEvent(node);
            });

            socket.connect();
        } catch (URISyntaxException e) {
            log.error("[CHZZK] 소켓 URL 오류", e);
        } catch (Exception e) {
            log.error("[CHZZK] 초기화 실패", e);
        }
    }

    /* --------------------------------------------------------------------- */
    /* SYSTEM 이벤트 처리                                                    */
    /* --------------------------------------------------------------------- */
    private void handleSystemEvent(JsonNode node) {
        String type = node.path("type").asText();
        if ("connected".equals(type)) {
            String sessionKey = node.path("data").path("sessionKey").asText();
            log.info("[CHZZK] 세션 키 수신: {}", sessionKey);
            subscribeChat(sessionKey);
        } else {
            log.debug("[CHZZK] SYSTEM 이벤트: {}", node.toPrettyString());
        }
    }

    /* --------------------------------------------------------------------- */
    /* CHAT 이벤트 처리                                                      */
    /* --------------------------------------------------------------------- */
    private void handleChatEvent(JsonNode node) {
        try {
            /* 채널/유저 필터링 */
            String senderId = node.path("senderChannelId").asText();
            String chId = node.path("channelId").asText();
            if (!channelId.equals(chId) || !targetUserChannelId.equals(senderId)) {
                return; // 관심 없는 메시지
            }

            /* 메시지 JSON 문자열 그대로 보관 */
            String json = om.writeValueAsString(node);
            chatHistory.add(json);

            /* 리스트 크기 유지(500개 초과 시 앞에서 제거) */
            if (chatHistory.size() > 500) {
                chatHistory.remove(0);
            }

            log.info("[CHZZK] {} → {}", senderId, node.path("content").asText());
        } catch (Exception e) {
            log.error("[CHZZK] CHAT 파싱 오류", e);
        }
    }


    // ─── 교체 후 : Client 세션 전용 ─────────────────────────────
    private String fetchClientSessionUrl() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.set("Client-Id", clientId);
        h.set("Client-Secret", clientSecret);

        HttpEntity<Void> req = new HttpEntity<>(h);
        String url = baseUrl + "/open/v1/sessions/auth/client";

        ResponseEntity<JsonNode> res =
                rest.exchange(url, HttpMethod.GET, req, JsonNode.class);

        String sessionUrl = res.getBody().path("content").path("url").asText();
        if (sessionUrl.isBlank()) {
            throw new IllegalStateException("세션 URL 파싱 실패 : " + res.getBody());
        }
        return sessionUrl;
    }



    // 세션 전용임 - 구버전
    /* --------------------------------------------------------------------- */
    /* REST API: 세션 URL, 구독 요청                                          */
    /* --------------------------------------------------------------------- */
    private String fetchSessionUrl() {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(accessToken);
        HttpEntity<Void> req = new HttpEntity<>(h);

        String url = baseUrl + "/open/v1/sessions/auth";
        ResponseEntity<JsonNode> res =
                rest.exchange(url, HttpMethod.GET, req, JsonNode.class);

        JsonNode body = res.getBody();
        if (body == null) {
            throw new IllegalStateException("[CHZZK] 세션 응답이 null");
        }

        String sessionUrl = body.path("content").path("url").asText();  // ← 핵심 수정
        if (sessionUrl.isBlank()) {
            log.error("[CHZZK] 세션 URL 파싱 실패 → 전체 응답: {}", body.toPrettyString());
            throw new IllegalStateException("세션 URL 파싱 실패");
        }
        return sessionUrl;
    }


    private void subscribeChat(String sessionKey) {
        String url = baseUrl + "/open/v1/sessions/events/subscribe/chat?sessionKey=" + sessionKey;

        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(accessToken);
        h.setContentType(MediaType.APPLICATION_JSON);

        /* CHZZK는 Body가 없어도 되지만, 빈 JSON 전송 */
        HttpEntity<String> req = new HttpEntity<>("{}", h);

        try {
            rest.exchange(url, HttpMethod.POST, req, Void.class);
            log.info("[CHZZK] 채팅 구독 요청 완료 (sessionKey={})", sessionKey);
        } catch (Exception e) {
            log.error("[CHZZK] 채팅 구독 실패", e);
        }
    }

    /* --------------------------------------------------------------------- */
    /* 수동 종료용 (테스트/재시작 시)                                        */
    /* --------------------------------------------------------------------- */
    public void stop() {
        if (socket != null && socket.connected()) {
            socket.disconnect();
            socket.close();
            log.info("[CHZZK] 소켓 연결 종료");
        }
    }

    /* ---------------- 공통 JSON 파싱 헬퍼 ---------------- */
    private JsonNode toJson(Object arg) {
        try {
            if (arg == null) return om.nullNode();
            if (arg instanceof String s) {           // 문자열
                return om.readTree(s);
            }
            if (arg instanceof org.json.JSONObject jo) { // JSONObject
                return om.readTree(jo.toString());
            }
            // 그 밖의 Map, List 등도 모두 Tree 로 변환
            return om.valueToTree(arg);
        } catch (Exception e) {
            log.error("[CHZZK] JSON 파싱 실패 : {}", arg, e);
            return om.nullNode();
        }
    }
}
