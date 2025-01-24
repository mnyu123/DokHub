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

    // =============================================================
    // 1) clip 전용
    // =============================================================
    @GetMapping("/clip")
    public List<ChannelDto> getClipChannels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size
    ) {
        // Service에서 category="clip" 으로 페이징
        return channelService.getChannelsPaged("clip", page, size);
    }

    @GetMapping("/clip/totalCount")
    public int getClipCount() {
        return channelService.getTotalCount("clip");
    }

    // =============================================================
    // 2) song 전용
    // =============================================================
    @GetMapping("/song")
    public List<ChannelDto> getSongChannels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size
    ) {
        // Service에서 category="song"
        return channelService.getChannelsPaged("song", page, size);
    }

    @GetMapping("/song/totalCount")
    public int getSongCount() {
        return channelService.getTotalCount("song");
    }

    // =============================================================
    // 3) main 전용
    // =============================================================
    @GetMapping("/main")
    public List<ChannelDto> getMainChannels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size
    ) {
        // Service에서 category="main"
        return channelService.getChannelsPaged("main", page, size);
    }

    @GetMapping("/main/totalCount")
    public int getMainCount() {
        return channelService.getTotalCount("main");
    }

    @GetMapping("/default_thumbnail.jpg")
    public ResponseEntity<Resource> getDefaultThumbnail() {
        Resource resource = (Resource) new ClassPathResource("static/default_thumbnail.jpg");
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

}