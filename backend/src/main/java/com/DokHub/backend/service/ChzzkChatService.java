package com.DokHub.backend.service;

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
import java.util.List;
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
            throw new RuntimeException("ChzzkChat 생성 실패", e);
        }
    }

    /* ────── 이벤트 핸들러 등록 ────── */
    private void registerEventHandlers(ChzzkChat c) {

        /* 연결 */
        c.on(ConnectEvent.class, evt -> {
            log.info("[DOKHUB] 채팅 소켓 연결 (재연결? {})", evt.isReconnecting());
            if (!evt.isReconnecting()) {
                c.requestRecentChat(50);
            }
        });

        /* 메시지 */
        c.on(ChatMessageEvent.class, evt -> {
            ChatMessage msg = evt.getMessage();
            if (msg.getProfile() == null) return;
            if (TARGET_USER_NICKNAME.equals(msg.getProfile().getNickname())) {
                chatHistory.add(msg.getContent());
                log.info("[CHAT] {}: {}", TARGET_USER_NICKNAME, msg.getContent());
            }
            // 테스트용 수집
            if (TARGET_USER_TEST.equals(msg.getProfile().getNickname())) {
                // chatHistory.add(msg.getContent()); // 굳이 안보내도 됨
                log.info("[CHAT TEST] {}: {}", TARGET_USER_TESTNAME, msg.getContent());
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
}
