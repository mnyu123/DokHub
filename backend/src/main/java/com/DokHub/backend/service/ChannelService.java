package com.DokHub.backend.service;

import com.DokHub.backend.dto.ChannelDto;
import com.DokHub.backend.dto.VideoInfoDto;
import com.DokHub.backend.entity.ChannelEntity;
import com.DokHub.backend.repository.ChannelRepository;
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

    /**
     * 채널 정보를 페이지네이션 형태로 가져오기
     */
    public List<ChannelDto> getChannelsPaged(String category, int page, int size) {
        List<ChannelEntity> entities = channelRepository.findByCategoryIgnoreCase(category);

        return entities.stream()
                .skip(page * size)
                .limit(size)
                .map(entity -> {
                    // ^^^ 캐시 버전 사용 예시: 최근 영상
                    List<VideoInfoDto> recentVideos = youTubeService.getRecentVideosCached(entity.getChannelId());

                    // ^^^ 캐시 버전 사용 예시: 채널 썸네일
                    String thumbnail = youTubeService.getChannelThumbnailCached(entity.getChannelId());

                    return new ChannelDto(
                            entity.getCategory(),
                            entity.getChannelName(),
                            entity.getChannelLink(),
                            // 기존 videoPreviewUrl 대신 캐시에서 가져온 썸네일 사용
                            thumbnail,
                            entity.getChannelId(),
                            recentVideos
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * 총 채널 수
     */
    public int getTotalCount(String category) {
        return channelRepository.findByCategoryIgnoreCase(category).size();
    }

    /**
     * ^^^ 예시: 특정 채널의 캐시를 비워주는 메서드 (Controller에서 호출 가능)
     */
    public void forceClearChannelCache(String channelId) {
        youTubeService.clearRecentVideosCache(channelId);
    }

    /**
     * ^^^ 예시: 특정 채널 썸네일을 비워주는 메서드 (Controller에서 호출 가능)
     */
    public void forceClearThumbnailsCache(List<String> channelIds) {
        youTubeService.clearThumbnailsCache(channelIds);
    }

    /**
     * ^^^ 예시: 전체 캐시 Refresh
     */
    public void forceRefreshAllCache() {
        youTubeService.refreshCache();
    }
}
