package com.DokHub.backend.controller;

import com.DokHub.backend.dto.VideoClickLogRequest;
import com.DokHub.backend.service.VideoClickLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/metrics")
public class VideoClickLogController {

    private final VideoClickLogService videoClickLogService;

    public VideoClickLogController(VideoClickLogService videoClickLogService) {
        this.videoClickLogService = videoClickLogService;
    }

    @PostMapping("/video-click")
    public ResponseEntity<String> saveVideoClick(@RequestBody VideoClickLogRequest request) {
        videoClickLogService.saveClick(request);
        return ResponseEntity.ok("OK");
    }
}