package com.DokHub.backend.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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

    // 실제 API 클라이언트 정보와 채널, 대상 사용자 정보로 수정하세요.
    private static final String API_CLIENT_ID = "fee1a177-b7ca-449c-a7ea-890966f3f27a";
    private static final String API_SECRET = "X087qpF8mx8udhwT1OWt8GtulCCPOLAqUyzTnxpI6dk"; // 임시 하드코딩
    private static final String CHANNEL_ID = "b68af124ae2f1743a1dcbf5e2ab41e0b"; // 독케익 방송 (생방일 때만 가능)
    // 대상 사용자를 고유 ID로 필터할 수도 있으나, 여기서는 닉네임 "쇼츠유입"으로 필터합니다.
    private static final String TARGET_USER_NICKNAME = "쇼츠유입";

    /**
     * -- GETTER --
     *  수집된 특정 유저(쇼츠유입 님)의 채팅 내역을 반환합니다.
     *
     * @return 채팅 메시지 문자열의 리스트
     */
    // 채팅 메시지를 저장할 스레드 안전한 리스트
    @Getter
    private final List<String> chatHistory = new CopyOnWriteArrayList<>();

    private final ChzzkClient client;
    private final ChzzkChat chat;

    public ChzzkChatService() {
        // API 클라이언트 생성 (필요하다면 withDebugMode() 등 추가 설정 가능)
        client = new ChzzkClientBuilder(API_CLIENT_ID, API_SECRET)
                // .withDebugMode() // 디버그 로그 활성화가 필요하면 주석 해제하세요.
                .build();

        // OpenAPI 인증이나 로그인 어댑터 설정이 필요하다면 아래 예시처럼 사용할 수 있습니다.
        // ChzzkSimpleUserLoginAdapter adapter = new ChzzkSimpleUserLoginAdapter("Access Token", null);
        // client = new ChzzkClientBuilder(API_CLIENT_ID, API_SECRET)
        //         .withLoginAdapter(adapter)
        //         .build();
        // client.loginAsync().join();

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

        // 채팅 메시지 이벤트 핸들러 등록 – 모든 수신 메시지를 로그로 출력하고,
        // 대상 사용자인 "쇼츠유입" 님의 메시지일 때만 chatHistory에 추가
        chat.on(ChatMessageEvent.class, evt -> {
            ChatMessage msg = evt.getMessage();

            if (msg.getProfile() != null) {
                String nickname = msg.getProfile().getNickname();
                log.info("[DOKHUB] : {} 님으로부터 메시지 수신됨, RoleCode: {}",
                        nickname,
                        msg.getProfile().getUserRoleCode());
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
        chat.connectAsync();
    }

}
