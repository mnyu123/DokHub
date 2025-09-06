package com.DokHub.backend.controller;

import com.DokHub.backend.dto.VideoInfoDto;
import com.DokHub.backend.service.YouTubeService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/playlist")
public class PlaylistController {

    private final YouTubeService youTubeService;

    public PlaylistController(YouTubeService youTubeService) {
        this.youTubeService = youTubeService;
    }

    @GetMapping("/{playlistId}")
    public List<VideoInfoDto> getPlaylistItems(
            @PathVariable String playlistId,
            @RequestParam(defaultValue = "25") int maxResults
    ) {
        int clamped = Math.max(1, Math.min(maxResults, 50));
        return youTubeService.getPlaylistItemsCached(playlistId, clamped);
    }
}