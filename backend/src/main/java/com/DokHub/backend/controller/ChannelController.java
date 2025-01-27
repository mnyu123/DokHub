package com.DokHub.backend.controller;

import com.DokHub.backend.dto.ChannelDto;
import com.DokHub.backend.service.ChannelService;
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

    /**
     * ^^^ 캐시를 전체 갱신(무효화) 시키는 엔드포인트
     */
    @PostMapping("/refreshCache")
    public ResponseEntity<String> refreshCache() {
        channelService.forceRefreshAllCache();
        return ResponseEntity.ok("[DOKHUB] : 전체 캐시를 갱신했습니다.");
    }

    /**
     * ^^^ 특정 채널의 비디오 캐시를 비워주는 엔드포인트
     */
    @PostMapping("/clearRecentVideosCache/{channelId}")
    public ResponseEntity<String> clearRecentVideosCache(@PathVariable String channelId) {
        channelService.forceClearChannelCache(channelId);
        return ResponseEntity.ok("[DOKHUB] : 채널 ID (" + channelId + ")의 최근 비디오 캐시를 비웠습니다.");
    }

    /**
     * ^^^ 특정 채널들의 썸네일 캐시를 비워주는 엔드포인트
     */
    @PostMapping("/clearThumbnailsCache")
    public ResponseEntity<String> clearThumbnailsCache(@RequestBody List<String> channelIds) {
        channelService.forceClearThumbnailsCache(channelIds);
        return ResponseEntity.ok("[DOKHUB] : 채널 ID 목록 " + channelIds + "의 썸네일 캐시를 비웠습니다.");
    }

}