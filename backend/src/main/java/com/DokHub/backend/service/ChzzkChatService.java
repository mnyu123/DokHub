package com.DokHub.backend.service;

import com.DokHub.backend.entity.ChatMessageEntity;
import com.DokHub.backend.repository.ChatMessageRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xyz.r2turntrue.chzzk4j.ChzzkClient;
import xyz.r2turntrue.chzzk4j.ChzzkClientBuilder;
import xyz.r2turntrue.chzzk4j.auth.ChzzkLegacyLoginAdapter;
import xyz.r2turntrue.chzzk4j.chat.ChatMessage;
import xyz.r2turntrue.chzzk4j.chat.ChzzkChat;
import xyz.r2turntrue.chzzk4j.chat.ChzzkChatBuilder;
import xyz.r2turntrue.chzzk4j.chat.event.ChatMessageEvent;
import xyz.r2turntrue.chzzk4j.chat.event.ConnectEvent;
import xyz.r2turntrue.chzzk4j.chat.event.ConnectionClosedEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ChzzkChatService {

    private static final String CHANNEL_ID = "b68af124ae2f1743a1dcbf5e2ab41e0b";
    private static final String TARGET_USER_NICKNAME = "독케익";
    private static final String TARGET_USER_TEST = "쇼츠유입";
    private static final int MAX_HISTORY = 100;

    @Value("${chzzk.client.id:}")
    private String apiClientId;
    @Value("${chzzk.client.secret:}")
    private String apiSecret;
    @Value("${chzzk.nid.aut:}")
    private String nidAut;
    @Value("${chzzk.nid.ses:}")
    private String nidSes;
    @Value("${chzzk.chat.enabled:true}")
    private boolean chatEnabled;

    private final ChatMessageRepository chatMessageRepository;
    private final List<String> inMemoryHistory = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private volatile boolean isChatConnected;
    private ChzzkClient client;
    private ChzzkChat chat;

    public ChzzkChatService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @PostConstruct
    public void init() {
        if (!isConfigured()) {
            log.info("[DOKHUB] Chzzk 채팅 연결이 비활성화되었거나 인증정보가 없습니다.");
            return;
        }
        log.info("[DOKHUB] Chzzk 채팅 연결 준비 완료. 방송 시작 시 연결합니다.");
    }

    private boolean isConfigured() {
        return chatEnabled
                && !isBlank(apiClientId)
                && !isBlank(apiSecret)
                && !isBlank(nidAut)
                && !isBlank(nidSes);
    }

    private synchronized void createClientAndChat(String aut, String ses) {
        if (!isConfigured()) {
            return;
        }
        try {
            closeChat();
            var adapter = new ChzzkLegacyLoginAdapter(aut, ses);
            client = new ChzzkClientBuilder(apiClientId, apiSecret)
                    .withLoginAdapter(adapter)
                    .build();
            client.loginAsync().join();
            chat = new ChzzkChatBuilder(client, CHANNEL_ID).build();
            registerEventHandlers(chat);
        } catch (IOException e) {
            throw new IllegalStateException("Chzzk 채팅 클라이언트 생성에 실패했습니다.", e);
        }
    }

    private void registerEventHandlers(ChzzkChat currentChat) {
        currentChat.on(ConnectEvent.class, event -> {
            isChatConnected = true;
            currentChat.requestRecentChat(50);
            log.info("[DOKHUB] Chzzk 채팅 소켓 연결 완료");
        });

        currentChat.on(ChatMessageEvent.class, event -> {
            ChatMessage message = event.getMessage();
            if (message.getProfile() == null) {
                return;
            }

            String nickname = message.getProfile().getNickname();
            String content = normalizeContent(message.getContent());
            if (content.isEmpty()) {
                return;
            }

            if (TARGET_USER_NICKNAME.equals(nickname)) {
                saveMessage(content);
                log.info("[CHAT] {}: {}", TARGET_USER_NICKNAME, content);
            } else if (TARGET_USER_TEST.equals(nickname)) {
                log.debug("[CHAT TEST] {}", content);
            }
        });

        currentChat.on(ConnectionClosedEvent.class, event -> {
            isChatConnected = false;
            log.warn("[DOKHUB] Chzzk 채팅 소켓 종료(code={}, reason={})", event.getCode(), event.getReason());
            if (event.getCode() == 4003 && isConfigured()) {
                scheduler.schedule(this::refreshCookiesAndReconnect, 30, TimeUnit.SECONDS);
            }
        });
    }

    private void saveMessage(String content) {
        inMemoryHistory.add(content);
        while (inMemoryHistory.size() > MAX_HISTORY) {
            inMemoryHistory.remove(0);
        }

        try {
            ChatMessageEntity entity = new ChatMessageEntity();
            entity.setContent(content);
            entity.setMessageTime(System.currentTimeMillis());
            entity.setSenderChannelId(CHANNEL_ID);
            chatMessageRepository.save(entity);
        } catch (RuntimeException exception) {
            log.error("[DOKHUB] 채팅 메시지 DB 저장 실패", exception);
        }
    }

    public List<String> getChatHistory() {
        try {
            List<String> result = new ArrayList<>(chatMessageRepository.findTop100ByOrderByMessageTimeDesc().stream()
                    .map(ChatMessageEntity::getContent)
                    .filter(content -> content != null && !content.isBlank())
                    .toList());
            Collections.reverse(result);
            return result;
        } catch (RuntimeException exception) {
            log.warn("[DOKHUB] 채팅 DB 조회 실패, 메모리 기록을 반환합니다.", exception);
            return List.copyOf(inMemoryHistory);
        }
    }

    private void refreshCookiesAndReconnect() {
        if (!isConfigured()) {
            return;
        }
        try {
            createClientAndChat(nidAut, nidSes);
            if (chat != null) {
                chat.connectAsync();
            }
        } catch (RuntimeException exception) {
            log.error("[DOKHUB] Chzzk 채팅 재연결 실패", exception);
            scheduler.schedule(this::refreshCookiesAndReconnect, 30, TimeUnit.SECONDS);
        }
    }

    public synchronized void ensureChatConnection(boolean currentlyLive) {
        if (!currentlyLive || !isConfigured()) {
            closeChat();
            return;
        }
        if (chat == null || !isChatConnected) {
            createClientAndChat(nidAut, nidSes);
            if (chat != null) {
                chat.connectAsync();
            }
        }
    }

    private synchronized void closeChat() {
        if (chat != null) {
            try {
                chat.closeAsync();
            } catch (RuntimeException exception) {
                log.debug("[DOKHUB] Chzzk 채팅 종료 중 오류", exception);
            }
        }
        chat = null;
        client = null;
        isChatConnected = false;
    }

    private String normalizeContent(String content) {
        if (content == null) {
            return "";
        }
        String normalized = content.strip();
        return normalized.length() <= 500 ? normalized : normalized.substring(0, 500);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    @PreDestroy
    public void shutdown() {
        closeChat();
        scheduler.shutdownNow();
    }
}
