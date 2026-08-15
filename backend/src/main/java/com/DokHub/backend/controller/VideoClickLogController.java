package com.DokHub.backend.controller;

import com.DokHub.backend.dto.VideoClickLogRequest;
import com.DokHub.backend.security.RequestRateLimiter;
import com.DokHub.backend.service.VideoClickLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@RestController
@RequestMapping("/api/metrics")
public class VideoClickLogController {

    private final VideoClickLogService videoClickLogService;
    private final RequestRateLimiter requestRateLimiter;

    public VideoClickLogController(VideoClickLogService videoClickLogService, RequestRateLimiter requestRateLimiter) {
        this.videoClickLogService = videoClickLogService;
        this.requestRateLimiter = requestRateLimiter;
    }

    @PostMapping("/video-click")
    public ResponseEntity<String> saveVideoClick(
            @Valid @RequestBody VideoClickLogRequest request,
            HttpServletRequest servletRequest) {
        String bucket = "video-click:" + servletRequest.getRemoteAddr();
        if (!requestRateLimiter.allow(bucket, 60, Duration.ofMinutes(1))) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "요청이 너무 많습니다.");
        }
        videoClickLogService.saveClick(request);
        return ResponseEntity.ok("OK");
    }
}
