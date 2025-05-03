package com.DokHub.backend.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
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

    // 인증용 쿠키 값
    @Value("${chzzk.nid.aut}")
    private String nidAut;

    @Value("${chzzk.nid.ses}")
    private String nidSes;

    // 테스트용 채널 ID (필요에 따라 변경)
    private static final String CHANNEL_ID = "d7ddd7585a271e55159ae47c0ce9a9dd";
    // 필터 대상 사용자의 닉네임 (테스트 : 쇼츠유입(나) , 실제 : 독케익(대상 스트리머))
    private static final String TARGET_USER_NICKNAME = "쇼츠유입";

    /**
     * -- GETTER --
     * 수집된 독케익의 채팅 내역을 반환합니다.
     *
     * @return 채팅 메시지 문자열의 리스트
     */
    @Getter
    private final List<String> chatHistory = new CopyOnWriteArrayList<>();

    private ChzzkClient client;
    private ChzzkChat chat;

    @PostConstruct
    public void init() {
        log.info("[DOKHUB] : API 클라이언트 생성 중... (ID: {})", apiClientId);

        // NID_AUT와 NID_SES 값을 사용하여 Legacy 인증 어댑터를 생성
        ChzzkLegacyLoginAdapter adapter = new ChzzkLegacyLoginAdapter(nidAut, nidSes);

        client = new ChzzkClientBuilder(apiClientId, apiSecret)
                .withLoginAdapter(adapter)
                .build();

        // 인증 수행
        client.loginAsync().join();
        log.info("[DOKHUB] : 사용자 인증 완료.");

        try {
            log.info("[DOKHUB] : 채팅 인스턴스 생성 중... (CHANNEL_ID: {})", CHANNEL_ID);
            chat = new ChzzkChatBuilder(client, CHANNEL_ID).build();
        } catch (IOException e) {
            throw new RuntimeException("[DOKHUB] : ChzzkChat 생성에 실패하였습니다.", e);
        }

        // 채팅 연결 이벤트 핸들러 등록
        chat.on(ConnectEvent.class, evt -> {
            log.info("[DOKHUB] : 채팅 서버에 연결되었습니다! (재연결 여부: {})", evt.isReconnecting());
            if (!evt.isReconnecting()) {
                log.info("[DOKHUB] : 최초 연결 성공, 최근 채팅 50개 요청합니다.");
                chat.requestRecentChat(50);
            }
        });

        // 채팅 메시지 이벤트 핸들러 등록 – 대상 사용자인 "쇼츠유입" 님의 메시지만 chatHistory에 추가
        chat.on(ChatMessageEvent.class, evt -> {
            ChatMessage msg = evt.getMessage();
            if (msg.getProfile() != null) {
                String nickname = msg.getProfile().getNickname();
                // 개발에만 체크할때 쓰고 , 운영에서는 로그가 너무 낭비됨
//                log.info("[DOKHUB] : {} 님으로부터 메시지 수신됨, RoleCode: {}",
//                        nickname, msg.getProfile().getUserRoleCode());
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