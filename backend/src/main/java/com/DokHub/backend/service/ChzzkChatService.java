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
import java.util.concurrent.ScheduledFuture;
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
    private boolean isChatConnecting;
    private boolean liveRequested;
    private boolean shuttingDown;
    private ScheduledFuture<?> reconnectTask;
    private ChzzkClient client;
    private volatile ChzzkChat chat;

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
            client = createChatClient(aut, ses);
            client.loginAsync().orTimeout(15, TimeUnit.SECONDS).join();
            chat = createChatSession(client);
            registerEventHandlers(chat);
        } catch (IOException e) {
            throw new IllegalStateException("Chzzk 채팅 클라이언트 생성에 실패했습니다.", e);
        }
    }

    ChzzkClient createChatClient(String aut, String ses) throws IOException {
        ChzzkLegacyLoginAdapter adapter = new ChzzkLegacyLoginAdapter(aut, ses);
        return new ChzzkClientBuilder(apiClientId, apiSecret)
                .withLoginAdapter(adapter)
                .build();
    }

    ChzzkChat createChatSession(ChzzkClient currentClient) throws IOException {
        return new ChzzkChatBuilder(currentClient, CHANNEL_ID).withAutoReconnect(false).build();
    }

    private void registerEventHandlers(ChzzkChat currentChat) {
        currentChat.on(ConnectEvent.class, event -> {
            synchronized (this) {
                if (currentChat != chat || !liveRequested || shuttingDown) {
                    currentChat.closeAsync();
                    return;
                }
                isChatConnected = true;
                isChatConnecting = false;
                currentChat.requestRecentChat(50);
                log.info("[DOKHUB] Chzzk 채팅 소켓 연결 완료");
            }
        });

        currentChat.on(ChatMessageEvent.class, event -> {
            if (currentChat != chat) {
                return;
            }
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
            synchronized (this) {
                if (currentChat != chat) {
                    return;
                }
                isChatConnected = false;
                isChatConnecting = false;
                log.warn("[DOKHUB] Chzzk 채팅 소켓 종료(code={}, reason={})", event.getCode(), event.getReason());
                scheduleReconnect(30);
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

    private synchronized void refreshCookiesAndReconnect() {
        reconnectTask = null;
        if (!liveRequested || shuttingDown || !isConfigured() || isChatConnected || isChatConnecting) {
            return;
        }
        try {
            createClientAndChat(nidAut, nidSes);
            if (chat != null) {
                isChatConnecting = true;
                ChzzkChat currentChat = chat;
                currentChat.connectAsync().orTimeout(20, TimeUnit.SECONDS).whenComplete((ignored, failure) -> {
                    synchronized (ChzzkChatService.this) {
                        if (currentChat != chat || failure == null) {
                            return;
                        }
                        log.warn("[DOKHUB] Chat connection failed", failure);
                        closeChat();
                        scheduleReconnect(30);
                    }
                });
            }
        } catch (RuntimeException exception) {
            log.error("[DOKHUB] Chzzk 채팅 재연결 실패", exception);
            closeChat();
            scheduleReconnect(30);
        }
    }

    public synchronized void updateChatConnection(boolean currentlyLive) {
        liveRequested = currentlyLive && isConfigured() && !shuttingDown;
        if (!liveRequested) {
            if (reconnectTask != null) {
                reconnectTask.cancel(false);
                reconnectTask = null;
            }
            closeChat();
            return;
        }
        if (!isChatConnected && !isChatConnecting) {
            scheduleReconnect(0);
        }
    }

    private synchronized void scheduleReconnect(long delaySeconds) {
        if (!liveRequested || shuttingDown || !isConfigured() || reconnectTask != null) {
            return;
        }
        reconnectTask = scheduler.schedule(this::refreshCookiesAndReconnect, delaySeconds, TimeUnit.SECONDS);
    }

    private synchronized void closeChat() {
        ChzzkChat previousChat = chat;
        ChzzkClient previousClient = client;
        chat = null;
        client = null;
        isChatConnected = false;
        isChatConnecting = false;
        if (previousChat != null) {
            try {
                previousChat.closeAsync().whenComplete((ignored, failure) -> releaseClient(previousClient));
                return;
            } catch (RuntimeException exception) {
                log.debug("[DOKHUB] Chzzk 채팅 종료 중 오류", exception);
            }
        }
        releaseClient(previousClient);
    }

    private void releaseClient(ChzzkClient previousClient) {
        if (previousClient != null) {
            previousClient.getHttpClient().dispatcher().cancelAll();
            previousClient.getHttpClient().connectionPool().evictAll();
            previousClient.getHttpClient().dispatcher().executorService().shutdown();
        }
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
    public synchronized void shutdown() {
        shuttingDown = true;
        updateChatConnection(false);
        scheduler.shutdownNow();
    }
}
