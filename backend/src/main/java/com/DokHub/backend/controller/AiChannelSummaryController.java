package com.DokHub.backend.controller;

import com.DokHub.backend.dto.AiChannelSummaryResponse;
import com.DokHub.backend.security.RequestRateLimiter;
import com.DokHub.backend.service.AiChannelSummaryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Set;
import java.time.Duration;

@RestController
@RequestMapping("/api/ai/channel")
public class AiChannelSummaryController {

    private static final Set<String> ALLOWED_CATEGORIES = Set.of("clip", "stclip", "song", "main");

    private final AiChannelSummaryService aiChannelSummaryService;
    private final RequestRateLimiter requestRateLimiter;

    public AiChannelSummaryController(
            AiChannelSummaryService aiChannelSummaryService,
            RequestRateLimiter requestRateLimiter) {
        this.aiChannelSummaryService = aiChannelSummaryService;
        this.requestRateLimiter = requestRateLimiter;
    }

    @GetMapping("/summary")
    public AiChannelSummaryResponse getSummary(
            @RequestParam(defaultValue = "clip") String category,
            @RequestParam(defaultValue = "14") int periodDays,
            HttpServletRequest request
    ) {
        if (!requestRateLimiter.allow("ai-summary:" + request.getRemoteAddr(), 10, Duration.ofMinutes(1))) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "요청이 너무 많습니다.");
        }
        String normalizedCategory = category == null ? "clip" : category.trim().toLowerCase(Locale.ROOT);
        if (!ALLOWED_CATEGORIES.contains(normalizedCategory)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "지원하지 않는 카테고리입니다.");
        }
        return aiChannelSummaryService.buildSummary(normalizedCategory, periodDays);
    }
}
