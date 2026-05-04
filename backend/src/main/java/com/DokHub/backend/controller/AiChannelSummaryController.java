package com.DokHub.backend.controller;

import com.DokHub.backend.dto.AiChannelSummaryResponse;
import com.DokHub.backend.service.AiChannelSummaryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/channel")
public class AiChannelSummaryController {

    private final AiChannelSummaryService aiChannelSummaryService;

    public AiChannelSummaryController(AiChannelSummaryService aiChannelSummaryService) {
        this.aiChannelSummaryService = aiChannelSummaryService;
    }

    @GetMapping("/summary")
    public AiChannelSummaryResponse getSummary(
            @RequestParam(defaultValue = "clip") String category,
            @RequestParam(defaultValue = "14") int periodDays
    ) {
        return aiChannelSummaryService.buildSummary(category, periodDays);
    }
}