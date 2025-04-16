package com.DokHub.backend.controller;

import com.DokHub.backend.service.ChzzkChatService;
import com.DokHub.backend.service.ChzzkLiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ChzzkLiveController {

    @Autowired
    private ChzzkLiveService chzzkLiveService;

    @Autowired
    private ChzzkChatService chzzkChatService;

    @GetMapping("/api/live/status")
    public Map<String, String> getLiveStatus() {
        boolean isLive = chzzkLiveService.isChannelLive();
        String status = isLive ? "on" : "off";
        return Map.of("livestatus", status);
    }

    @GetMapping("/api/chat/history") // v0.9 신규기능 독채팅
    public List<String> getChatHistory() {
        return chzzkChatService.getChatHistory();
    }
}
