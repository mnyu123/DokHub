package com.DokHub.backend.controller;

import com.DokHub.backend.dto.ChannelDto;
import com.DokHub.backend.service.ChannelService;
import jakarta.annotation.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    // 기본 경로("/")
    @GetMapping("/")
    public String home() {
        return "Dokhub 404 notnot found";
    }

    // 2025.1.25 변경된 효율적 컨트롤러
    @GetMapping("/{category}")
    public List<ChannelDto> getChannels(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size
    ) {
        return channelService.getChannelsPaged(category, page, size);
    }

    @GetMapping("/{category}/totalCount")
    public int getTotalCount(@PathVariable String category) {
        return channelService.getTotalCount(category);
    }

}