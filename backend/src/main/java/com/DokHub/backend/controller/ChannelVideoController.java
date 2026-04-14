package com.DokHub.backend.controller;

import com.DokHub.backend.dto.VideoInfoDto;
import com.DokHub.backend.service.YouTubeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/channel-videos")
public class ChannelVideoController {

    private final YouTubeService youTubeService;

    public ChannelVideoController(YouTubeService youTubeService) {
        this.youTubeService = youTubeService;
    }

    @GetMapping("/{channelId}")
    public List<VideoInfoDto> getChannelVideos(
            @PathVariable String channelId,
            @RequestParam(defaultValue = "25") int maxResults
    ) {
        int clamped = Math.max(1, Math.min(maxResults, 25));
        return youTubeService.getChannelVideosCached(channelId, clamped);
    }
}
