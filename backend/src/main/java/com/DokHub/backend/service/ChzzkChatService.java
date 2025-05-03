package com.DokHub.backend.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xyz.r2turntrue.chzzk4j.ChzzkClient;
import xyz.r2turntrue.chzzk4j.ChzzkClientBuilder;
import xyz.r2turntrue.chzzk4j.auth.ChzzkLegacyLoginAdapter;
import xyz.r2turntrue.chzzk4j.chat.*;
import xyz.r2turntrue.chzzk4j.chat.event.*;
import xyz.r2turntrue.chzzk4j.types.channel.live.ChzzkLiveStatus;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Service
public class ChzzkChatService {

    /* ────── 설정 값 ────── */
    @Value("${chzzk.client.id}")     private String apiClientId;
    @Value("${chzzk.client.secret}") private String apiSecret;
    @Value("${chzzk.nid.aut}")       private String nidAut;
    @Value("${chzzk.nid.ses}")       private String nidSes;

    /** 모니터링 대상 채널 ID·닉네임 */
    private static final String CHANNEL_ID = "b68af124ae2f1743a1dcbf5e2ab41e0b";
    private static final String TARGET_USER_NICKNAME = "독케익";

    /** 최근 채팅 내역 – Thread‑safe */
    @Getter
    private final List<String> chatHistory = new CopyOnWriteArrayList<>();

    /* ────── 내부 상태 ────── */
    private ChzzkClient client;
    private ChzzkChat   chat;

    /** 재연결·라이브 상태 Poller */
    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    private static final Duration LIVE_POLL_INTERVAL = Duration.ofSeconds(20);

    /* ────── Life‑cycle ────── */

    @PostConstruct
    public void init() {
        log.info("[DOKHUB] chzzk4j 초기화 시작…");

        // 1) 클라이언트 + 인증
        var adapter = new ChzzkLegacyLoginAdapter(nidAut, nidSes);
        client = new ChzzkClientBuilder(apiClientId, apiSecret)
                .withLoginAdapter(adapter)
                .build();
        client.loginAsync().join();
        log.info("[DOKHUB] 사용자 인증 완료");

        // 2) 최초 라이브 확인 & 연결
        connectIfLive();

        // 3) 라이브 상태 주기 체크
        scheduler.scheduleAtFixedRate(
                this::connectIfLive,
                LIVE_POLL_INTERVAL.toSeconds(),
                LIVE_POLL_INTERVAL.toSeconds(),
                TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdownNow();
        if (chat != null) chat.closeAsync();
    }

    /* ────── Core Logic ────── */

    /** 라이브 중이면 채팅 인스턴스 생성/재연결 */
    private synchronized void connectIfLive() {
        try {
            ChzzkLiveStatus live = client.fetchLiveStatus(CHANNEL_ID);
            if (!live.isOnline()) {
                log.debug("[DOKHUB] 방송 OFF → 연결 보류");
                return;
            }
        } catch (Exception e) {
            log.warn("[DOKHUB] 라이브 상태 조회 실패: {}", e.getMessage());
            return;
        }

        /* 이미 연결돼 있으면 Skip */
        if (chat != null && chat.isConnectedToChat()) {
            return;
        }

        try {
            if (chat == null) {
                log.info("[DOKHUB] 채팅 인스턴스 생성");
                chat = new ChzzkChatBuilder(client, CHANNEL_ID).build();
                registerEventHandlers();
            }
            log.info("[DOKHUB] 채팅 서버 연결 시도…");
            chat.connectAsync();
        } catch (IOException ex) {
            log.error("[DOKHUB] ChzzkChat 생성/연결 실패", ex);
        }
    }

    /** 이벤트 핸들러 등록 (최초 1회) */
    private void registerEventHandlers() {

        /* 연결 */
        chat.on(ConnectEvent.class, evt -> {
            log.info("[DOKHUB] 채팅 소켓 연결 — 재연결 여부: {}", evt.isReconnecting());
            if (!evt.isReconnecting()) chat.requestRecentChat(50);
        });

        /* 메시지 수신 */
        chat.on(ChatMessageEvent.class, evt -> {
            ChatMessage msg = evt.getMessage();
            String nick = msg.getProfile() == null ? null : msg.getProfile().getNickname();
            if (TARGET_USER_NICKNAME.equals(nick)) {
                chatHistory.add(msg.getContent());
                log.info("[CHAT] {}: {}", nick, msg.getContent());
            }
        });

        /* 끊김 → 재연결 예약 */
        chat.on(ConnectionClosedEvent.class, evt -> {
            int code = evt.getCode();
            log.warn("[DOKHUB] 소켓 종료(code={}, reason={})", code, evt.getReason());

            long delay = (code == 4003) ? 1 : 5;  // 성인 제한 등은 1초, 일반 오류는 5초
            scheduler.schedule(() -> {
                log.info("[DOKHUB] {}초 뒤 재연결 시도", delay);
                connectIfLive();
            }, delay, TimeUnit.SECONDS);
        });
    }
}
