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

    // 기본 경로("/") 매핑
    @GetMapping("/")
    public String home() {
        return "Dokhub 404 notnot found";
    }

    /**
     * 예: /api/channels?page=0
     * 페이지가 없으면 기본값(page=0)으로
     */
    @GetMapping("/api/channels")
    public List<ChannelDto> getChannels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size
    ) {
        return channelService.getChannelsPaged(page, size);
    }

    /**
     * 전체 채널 수나 총 페이지 수 등을 확인하고 싶을 때
     * (필요하면 클라이언트에서 이용)
     */
    @GetMapping("/api/channels/totalCount")
    public int getChannelsTotalCount() {
        return channelService.getTotalCount();
    }
}
