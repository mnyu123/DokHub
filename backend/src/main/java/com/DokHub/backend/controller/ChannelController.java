package com.DokHub.backend.controller;

import com.DokHub.backend.dto.ChannelDto;
import com.DokHub.backend.service.ChannelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ChannelController {

    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @GetMapping("/")
    public String home() {
        return "Dokhub 404 notnot found";
    }

    /**
     * 예: /api/channels?category=clip&page=0&size=7
     * category 기본값 "clip" (필요하면 "clip" 대신 "" 로 해서 전체?)
     */
    @GetMapping("/api/channels")
    public List<ChannelDto> getChannels(
            @RequestParam(defaultValue = "clip") String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size
    ) {
        return channelService.getChannelsPaged(category, page, size);
    }

    /**
     * 전체 채널 수(카테고리별)
     * 예: /api/channels/totalCount?category=clip
     */
    @GetMapping("/api/channels/totalCount")
    public int getChannelsTotalCount(
            @RequestParam(defaultValue = "clip") String category
    ) {
        return channelService.getTotalCount(category);
    }
}
