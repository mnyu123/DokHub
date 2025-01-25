package com.DokHub.backend.service;

import com.DokHub.backend.dto.ChannelDto;
import com.DokHub.backend.dto.VideoInfoDto;
import com.DokHub.backend.entity.ChannelEntity;
import com.DokHub.backend.repository.ChannelRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final YouTubeService youTubeService;

    public ChannelService(ChannelRepository channelRepository, YouTubeService youTubeService) {
        this.channelRepository = channelRepository;
        this.youTubeService = youTubeService;
    }

    public List<ChannelDto> getChannelsPaged(String category, int page, int size) {
        List<ChannelEntity> entities = channelRepository.findByCategoryIgnoreCase(category);

        return entities.stream()
                .skip(page * size)
                .limit(size)
                .map(entity -> {
                    List<VideoInfoDto> recentVideos = youTubeService.getRecentVideos(entity.getChannelId());
                    return new ChannelDto(
                            entity.getCategory(),
                            entity.getChannelName(),
                            entity.getChannelLink(),
                            entity.getVideoPreviewUrl(),
                            entity.getChannelId(),
                            recentVideos
                    );
                })
                .collect(Collectors.toList());
    }

    public int getTotalCount(String category) {
        return (int) channelRepository.findByCategoryIgnoreCase(category).size();
    }
}
