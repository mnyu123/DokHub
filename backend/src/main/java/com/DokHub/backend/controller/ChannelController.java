package com.DokHub.backend.controller;

import com.DokHub.backend.dto.ChannelDto;
import com.DokHub.backend.security.AdminAuthorizationService;
import com.DokHub.backend.service.ChannelService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    private static final Set<String> ALLOWED_CATEGORIES = Set.of("clip", "stclip", "song", "main");

    private final ChannelService channelService;
    private final AdminAuthorizationService adminAuthorizationService;

    public ChannelController(ChannelService channelService, AdminAuthorizationService adminAuthorizationService) {
        this.channelService = channelService;
        this.adminAuthorizationService = adminAuthorizationService;
    }

    @GetMapping("/")
    public String home() {
        return "DokHub channels API";
    }

    @GetMapping("/{category}")
    public List<ChannelDto> getChannels(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        String safeCategory = requireCategory(category);
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, Math.min(size, 50));
        return channelService.getChannelsPaged(safeCategory, safePage, safeSize);
    }

    @GetMapping("/{category}/totalCount")
    public int getTotalCount(@PathVariable String category) {
        return channelService.getTotalCount(requireCategory(category));
    }

    @PostMapping("/refreshCache")
    public ResponseEntity<String> refreshCache(
            @RequestHeader(value = "X-Admin-Key", required = false) String adminKey) {
        adminAuthorizationService.requireAuthorized(adminKey);
        channelService.forceRefreshAllCache();
        return ResponseEntity.ok("전체 캐시를 갱신했습니다.");
    }

    @PostMapping("/clearRecentVideosCache/{channelId}")
    public ResponseEntity<String> clearRecentVideosCache(
            @PathVariable String channelId,
            @RequestHeader(value = "X-Admin-Key", required = false) String adminKey) {
        adminAuthorizationService.requireAuthorized(adminKey);
        channelService.forceClearChannelCache(channelId);
        return ResponseEntity.ok("채널의 최근 영상 캐시를 비웠습니다.");
    }

    @PostMapping("/clearThumbnailsCache")
    public ResponseEntity<String> clearThumbnailsCache(
            @RequestBody List<String> channelIds,
            @RequestHeader(value = "X-Admin-Key", required = false) String adminKey) {
        adminAuthorizationService.requireAuthorized(adminKey);
        channelService.forceClearThumbnailsCache(channelIds.stream().limit(50).toList());
        return ResponseEntity.ok("채널 썸네일 캐시를 비웠습니다.");
    }

    private String requireCategory(String category) {
        String normalized = category == null ? "" : category.trim().toLowerCase(Locale.ROOT);
        if (!ALLOWED_CATEGORIES.contains(normalized)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "지원하지 않는 카테고리입니다.");
        }
        return normalized;
    }
}
