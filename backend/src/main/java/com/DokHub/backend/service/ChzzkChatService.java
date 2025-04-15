package com.DokHub.backend.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xyz.r2turntrue.chzzk4j.ChzzkClient;
import xyz.r2turntrue.chzzk4j.ChzzkClientBuilder;
import xyz.r2turntrue.chzzk4j.chat.ChatMessage;
import xyz.r2turntrue.chzzk4j.chat.ChzzkChat;
import xyz.r2turntrue.chzzk4j.chat.ChzzkChatBuilder;
import xyz.r2turntrue.chzzk4j.chat.event.ChatMessageEvent;
import xyz.r2turntrue.chzzk4j.chat.event.ConnectEvent;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class ChzzkChatService {

    @Value("${chzzk.client.id}")
    private String apiClientId;

    @Value("${chzzk.client.secret}")
    private String apiSecret;

    private static final String CHANNEL_ID = "4de764d9dad3b25602284be6db3ac647"; // 테스트용 채널
    private static final String TARGET_USER_NICKNAME = "쇼츠유입"; // 필터 대상 사용자의 닉네임

    /**
     * -- GETTER --
     * 수집된 특정 유저(쇼츠유입 님)의 채팅 내역을 반환합니다.
     *
     * @return 채팅 메시지 문자열의 리스트
     */
    @Getter
    private final List<String> chatHistory = new CopyOnWriteArrayList<>();

    private ChzzkClient client;
    private ChzzkChat chat;

    @PostConstruct
    public void init() {
        // API 클라이언트 생성 (필요하다면 withDebugMode() 등 추가 설정 가능)
        client = new ChzzkClientBuilder(apiClientId, apiSecret)
                // .withDebugMode() // 필요시 활성화
                .build();

        // 채팅 연결을 위한 인스턴스 생성 (채널 ID 필요)
        try {
            chat = new ChzzkChatBuilder(client, CHANNEL_ID).build();
        } catch (IOException e) {
            throw new RuntimeException("[DOKHUB] : ChzzkChat 생성에 실패하였습니다.", e);
        }

        // 채팅 연결 이벤트 핸들러 등록
        chat.on(ConnectEvent.class, evt -> {
            log.info("[DOKHUB] : 채팅 서버에 연결되었습니다! (재연결 여부: {})", evt.isReconnecting());
            if (!evt.isReconnecting()) {
                // 재연결이 아닐 경우 최근 채팅 50개 요청
                chat.requestRecentChat(50);
            }
        });

        // 채팅 메시지 이벤트 핸들러 등록 – 수신 메시지를 로그로 출력하고,
        // 대상 사용자인 "쇼츠유입" 님의 메시지일 때만 chatHistory에 추가
        chat.on(ChatMessageEvent.class, evt -> {
            ChatMessage msg = evt.getMessage();
            if (msg.getProfile() != null) {
                String nickname = msg.getProfile().getNickname();
//                log.info("[DOKHUB] : {} 님으로부터 메시지 수신됨, RoleCode: {}",
//                        nickname,
//                        msg.getProfile().getUserRoleCode());
                if (TARGET_USER_NICKNAME.equals(nickname)) {
                    chatHistory.add(msg.getContent());
                    log.info("[DOKHUB] : {} 님의 메시지를 채팅 기록에 추가함: {}",
                            nickname, msg.getContent());
                }
            } else {
                log.info("[DOKHUB] : 익명 메시지 수신: {}", msg.getContent());
            }
        });

        // 채팅 서버에 비동기로 연결 시작
        log.info("[DOKHUB] : 채팅 서버 연결 시도 중...");
        chat.connectAsync();
    }
}
