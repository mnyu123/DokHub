package com.DokHub.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xyz.r2turntrue.chzzk4j.ChzzkClient;
import xyz.r2turntrue.chzzk4j.ChzzkClientBuilder;
import xyz.r2turntrue.chzzk4j.auth.ChzzkLegacyLoginAdapter;
import xyz.r2turntrue.chzzk4j.chat.*;
import xyz.r2turntrue.chzzk4j.chat.event.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Service
public class ChzzkChatService {

    /* ────── 설정 값 ────── */
    @Value("${chzzk.client.id}")
    private String apiClientId;
    @Value("${chzzk.client.secret}")
    private String apiSecret;
    @Value("${chzzk.nid.aut}")
    private String nidAut;
    @Value("${chzzk.nid.ses}")
    private String nidSes;

    // 임시 테스트용 니니아 f00f6d46ecc6d735b96ecf376b9e5212
    // 독케익 채널 아이디 b68af124ae2f1743a1dcbf5e2ab41e0b
    private static final String CHANNEL_ID = "b68af124ae2f1743a1dcbf5e2ab41e0b";
    private static final String TARGET_USER_NICKNAME = "독케익";
    private static final String TARGET_USER_TEST = "쇼츠유입";
    private static final String TARGET_USER_TESTNAME = "테스트용 수집";

    @Getter
    private final List<String> chatHistory = new CopyOnWriteArrayList<>();

    private ChzzkClient client;
    private ChzzkChat chat;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    // 실제 웹소켓 연결 상태를 추적하기 위한 변수
    private boolean isChatConnected = false;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /* ────── 초기 설정 ────── */

    @PostConstruct
    public void init() {
        log.info("[DOKHUB] API 클라이언트 생성(ID={})", apiClientId);

        createClientAndChat(nidAut, nidSes);
        chat.connectAsync();                         // 최초 연결
    }

    /* ────── 클라이언트+채팅 재생성 ────── */
    private void createClientAndChat(String aut, String ses) {
        try {
            if (chat != null) chat.closeAsync();

            var adapter = new ChzzkLegacyLoginAdapter(aut, ses);
            client = new ChzzkClientBuilder(apiClientId, apiSecret)
                    .withLoginAdapter(adapter)
                    .build();
            client.loginAsync().join();
            log.info("[DOKHUB] 사용자 인증 완료");

            chat = new ChzzkChatBuilder(client, CHANNEL_ID).build();
            registerEventHandlers(chat);

        } catch (IOException e) {
            throw new RuntimeException("[DOKHUB] : ChzzkChat 생성 실패", e);
        }
    }

    /* ────── 이벤트 핸들러 등록 ────── */
    private void registerEventHandlers(ChzzkChat c) {

        /* 연결 */
        c.on(ConnectEvent.class, evt -> {
            log.info("[DOKHUB] 채팅 소켓 연결 | 최근 채팅 50개 요청 (재연결 여부 확인 : {})", evt.isReconnecting());
            /* 재연결 여부와 관계없이 매번 50개 요청 */
            c.requestRecentChat(50);
        });

        /* 메시지 */
        c.on(ChatMessageEvent.class, evt -> {
            ChatMessage msg = evt.getMessage();
            if (msg.getProfile() == null) return;

            String nickname = msg.getProfile().getNickname();

            // 1. 수집 대상(독케익 또는 테스트계정)인지 확인
            if (TARGET_USER_NICKNAME.equals(nickname) || TARGET_USER_TEST.equals(nickname)) {

                // 원본 텍스트 가져오기
                String text = msg.getContent();

                // 2. 이모지 파싱 및 치환 로직 (공통 적용)
                try {
                    String rawJson = msg.getRawJson();

                    if (rawJson != null) {
                        JsonNode rootNode = objectMapper.readTree(rawJson);
                        JsonNode extrasNode = rootNode.path("extras");

                        if (extrasNode.isTextual()) {
                            extrasNode = objectMapper.readTree(extrasNode.asText());
                        }

                        JsonNode emojisNode = extrasNode.path("emojis");

                        if (!emojisNode.isMissingNode() && emojisNode.isObject()) {
                            Iterator<Map.Entry<String, JsonNode>> fields = emojisNode.fields();

                            while (fields.hasNext()) {
                                Map.Entry<String, JsonNode> field = fields.next();
                                String code = field.getKey();
                                String imageUrl = field.getValue().asText();

                                String target = "{:" + code + ":}";
                                String imgTag = "<img src='" + imageUrl + "' alt='" + code + "' style='width:24px; height:24px; display:inline-block; vertical-align:middle;' />";

                                text = text.replace(target, imgTag);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("[DOKHUB] 이모지 파싱 중 에러 발생", e);
                }

                // 3. 이모지가 치환된 최종 텍스트를 프론트엔드 전송용 리스트에 추가
                chatHistory.add(text);

                // 4. 로그 출력 분기 (콘솔 확인용)
                if (TARGET_USER_NICKNAME.equals(nickname)) {
                    log.info("[CHAT] {}: {}", TARGET_USER_NICKNAME, text);
                } else {
                    log.info("[CHAT TEST] {}: {}", TARGET_USER_TESTNAME, text);
                }
            }
        });

        /* 끊김 */
        c.on(ConnectionClosedEvent.class, evt -> {
            int code = evt.getCode();
            log.warn("[DOKHUB] 소켓 종료(code={}, reason={})", code, evt.getReason());

            if (code == 4003) {              // 쿠키 만료
                log.warn("[DOKHUB] 4003 – 쿠키 재생성 시도");
                scheduler.schedule(this::refreshCookiesAndReconnect, 1, TimeUnit.SECONDS);
            }
            /* 그 외는 라이브러리 기본 재연결에 맡김 */
        });
    }

    /* ────── 쿠키 새로 받아 재연결 (기본: 기존 값 재사용) ────── */
    private void refreshCookiesAndReconnect() {
        try {
            /* TODO: WebDriver 등으로 새 쿠키를 받아오려면 아래 두 줄을 교체 */
            String newAut = nidAut;   // ← 새 NID_AUT
            String newSes = nidSes;   // ← 새 NID_SES

            createClientAndChat(newAut, newSes);
            chat.connectAsync();
            log.info("[DOKHUB] 새 쿠키로 재연결 완료");

        } catch (Exception e) {
            log.error("[DOKHUB] 쿠키 재발급·재연결 실패, 30초 뒤 재시도", e);
            scheduler.schedule(this::refreshCookiesAndReconnect, 30, TimeUnit.SECONDS);
        }
    }

    /**
     * 주기적으로(또는 라이브 상태가 변경될 때) 호출되는 방어 로직
     */
    public void ensureChatConnection(boolean isCurrentlyLive) {
        if (isCurrentlyLive) {
            // 방송이 켜져 있는데 연결이 유실된 경우 (이벤트 핸들러에서 상태 갱신됨)
            if (chat == null || !isChatConnected) {
                log.info("[DOKHUB] 방송(ON-AIR) 중이나 채팅 세션이 끊어져 있습니다. 재연결을 시도합니다.");

                try {
                    if (chat != null) {
                        chat.connectAsync();
                    } else {
                        // 객체 자체가 없어진 경우 기존 레거시 초기화 함수 재사용
                        createClientAndChat(nidAut, nidSes);
                        chat.connectAsync();
                    }
                } catch (Exception e) {
                    log.error("[DOKHUB-CHAT] 채팅 재연결 실패", e);
                }
            }
        } else {
            // 방송이 꺼져 있는데 연결이 유지되고 있는 경우 안전하게 자원 해제
            if (chat != null && isChatConnected) {
                log.info("[DOKHUB] 방송이 종료되었습니다. 기존 채팅 세션을 안전하게 종료합니다.");
                try {
                    chat.closeAsync();
                } catch (Exception e) {
                    log.error("[DOKHUB-CHAT] 채팅 세션 종료 실패", e);
                }
                isChatConnected = false;
            }
        }
    }
}
