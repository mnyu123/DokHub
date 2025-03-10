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
}
