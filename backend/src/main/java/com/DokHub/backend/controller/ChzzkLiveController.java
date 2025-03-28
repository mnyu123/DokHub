package com.DokHub.backend.controller;

import com.DokHub.backend.service.ChzzkLiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ChzzkLiveController {

    @Autowired
    private ChzzkLiveService chzzkLiveService;

    @GetMapping("/api/live/status")
    public Map<String, String> getLiveStatus() {
        boolean isLive = chzzkLiveService.isChannelLive();
        String status = isLive ? "on" : "off";
        return Map.of("livestatus", status);
    }

    @GetMapping("/api/chat/dokchat") // v0.9 신규기능 개발용
    public String getChatPageInfo() {
        // 독채팅 관련 데이터나 간단한 메시지를 반환
        return "독채팅 페이지에 오신 것을 환영합니다.";
    }
}
