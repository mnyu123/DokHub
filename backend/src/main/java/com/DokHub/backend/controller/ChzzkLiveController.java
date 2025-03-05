package com.DokHub.backend.controller;

import com.DokHub.backend.service.ChzzkLiveService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/live")
public class ChzzkLiveController {

    private final ChzzkLiveService liveService;

    public ChzzkLiveController(ChzzkLiveService liveService) {
        this.liveService = liveService;
    }

    /**
     * 독케익 채널의 현재 방송 상태(라이브 on/off)를 조회합니다.
     * 예를 들어, defaultLiveTitle에 "방송 중"이라는 문자열이 포함되어 있으면 on, 그렇지 않으면 off로 판단합니다.
     */
    @GetMapping("/status")
    public ResponseEntity<?> getLiveStatus() {
        Map<String, Object> settings = liveService.getLiveSettings();
        String title = settings.get("defaultLiveTitle") != null ? settings.get("defaultLiveTitle").toString() : "";
        boolean isLive = title.contains("방송 중");
        Map<String, Object> result = new HashMap<>();
        result.put("liveStatus", isLive ? "on" : "off");
        return ResponseEntity.ok(result);
    }
}
