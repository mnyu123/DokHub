package com.DokHub.backend.service;

import com.DokHub.backend.dto.ChannelDto;
import com.DokHub.backend.dto.VideoInfoDto;
import com.DokHub.backend.entity.ChannelEntity;
import com.DokHub.backend.repository.ChannelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
     * 페이징 + 배치 썸네일 호출 적용
     */
    @Transactional
    public List<ChannelDto> getChannelsPaged(String category, int page, int size) {
        // 0) DB에서 페이지 단위로 채널 조회 (최신 업로드 내림차순)
        Page<ChannelEntity> entityPage = channelRepository
                .findByCategoryIgnoreCaseOrderByLatestUploadDesc(category, PageRequest.of(page, size));
        List<ChannelEntity> entities = entityPage.getContent();

        // 1) latestUpload == null 인 채널만 모아서 업데이트(2025-06-15 추가)
        List<ChannelEntity> toUpdate = entities.stream()
                .filter(ent -> ent.getLatestUpload() == null)
                .peek(ent -> {
                    List<VideoInfoDto> vids = youTubeService.getRecentVideosCached(ent.getChannelId());
                    if (!vids.isEmpty()) {
                        ent.setLatestUpload(vids.get(0).getPublishedAt());
                    }
                })
                .filter(ent -> ent.getLatestUpload() != null)
                .collect(Collectors.toList());
        if (!toUpdate.isEmpty()) {
            channelRepository.saveAll(toUpdate);
        }

        // 2) 배치로 썸네일 한 번에 가져오기 (YouTube API 호출 1회)
        List<String> channelIds = entities.stream()
                .map(ChannelEntity::getChannelId)
                .toList();
        Map<String,String> thumbnailMap = youTubeService.getChannelThumbnailsBatch(channelIds);

        // 3) DTO 변환 (recentVideos는 캐시 우선)
        return entities.stream()
                .map(ent -> {
                    List<VideoInfoDto> recentVideos = youTubeService.getRecentVideosCached(ent.getChannelId());
                    String thumbnail = thumbnailMap.getOrDefault(ent.getChannelId(), "");
                    return new ChannelDto(
                            ent.getCategory(),
                            ent.getChannelName(),
                            ent.getChannelLink(),
                            thumbnail,
                            ent.getChannelId(),
                            recentVideos
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * 총 채널 수
     */
    public int getTotalCount(String category) {
        return (int) channelRepository
                .findByCategoryIgnoreCaseOrderByLatestUploadDesc(category, PageRequest.of(0, Integer.MAX_VALUE))
                .getTotalElements();
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
