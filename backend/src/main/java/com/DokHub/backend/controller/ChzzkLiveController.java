package com.DokHub.backend.controller;

import com.DokHub.backend.security.RequestRateLimiter;
import com.DokHub.backend.service.ChzzkChatService;
import com.DokHub.backend.service.ChzzkLiveService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@RestController
public class ChzzkLiveController {

    private final ChzzkLiveService chzzkLiveService;
    private final ChzzkChatService chzzkChatService;
    private final RequestRateLimiter requestRateLimiter;

    public ChzzkLiveController(
            ChzzkLiveService chzzkLiveService,
            ChzzkChatService chzzkChatService,
            RequestRateLimiter requestRateLimiter) {
        this.chzzkLiveService = chzzkLiveService;
        this.chzzkChatService = chzzkChatService;
        this.requestRateLimiter = requestRateLimiter;
    }

    @GetMapping("/api/live/status")
    public Map<String, String> getLiveStatus(HttpServletRequest request) {
        enforceRateLimit("live-status:", request, 60);
        return Map.of("livestatus", chzzkLiveService.isChannelLive() ? "on" : "off");
    }

    @GetMapping("/api/chat/history")
    public List<String> getChatHistory(HttpServletRequest request) {
        enforceRateLimit("chat-history:", request, 30);
        return chzzkChatService.getChatHistory();
    }

    private void enforceRateLimit(String prefix, HttpServletRequest request, int limit) {
        if (!requestRateLimiter.allow(prefix + request.getRemoteAddr(), limit, Duration.ofMinutes(1))) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "요청이 너무 많습니다.");
        }
    }
}
