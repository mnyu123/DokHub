package com.DokHub.backend.controller;

import com.DokHub.backend.dto.ClickAnalyticsResponse;
import com.DokHub.backend.security.RequestRateLimiter;
import com.DokHub.backend.service.ClickAnalyticsAgentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@RestController
@RequestMapping("/api/analytics")
public class ClickAnalyticsController {

    private final ClickAnalyticsAgentService clickAnalyticsAgentService;
    private final RequestRateLimiter requestRateLimiter;

    public ClickAnalyticsController(
            ClickAnalyticsAgentService clickAnalyticsAgentService,
            RequestRateLimiter requestRateLimiter
    ) {
        this.clickAnalyticsAgentService = clickAnalyticsAgentService;
        this.requestRateLimiter = requestRateLimiter;
    }

    @GetMapping("/clicks")
    public ClickAnalyticsResponse getClickAnalytics(
            @RequestParam(defaultValue = "30") int periodDays,
            @RequestParam(defaultValue = "5") int limit,
            HttpServletRequest request
    ) {
        if (!requestRateLimiter.allow(
                "click-analytics:" + request.getRemoteAddr(), 30, Duration.ofMinutes(1))) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "요청이 너무 많습니다.");
        }
        return clickAnalyticsAgentService.getSnapshot(periodDays, limit);
    }
}
