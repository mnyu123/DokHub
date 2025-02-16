package com.DokHub.backend.service;

import com.DokHub.backend.dto.ChannelDto;
import com.DokHub.backend.dto.VideoInfoDto;
import com.DokHub.backend.entity.ChannelEntity;
import com.DokHub.backend.repository.ChannelRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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
     * 채널 정보를 최신 업로드 시간 기준으로 정렬하여 페이징 처리
     */
    public List<ChannelDto> getChannelsPaged(String category, int page, int size) {
        // 1. 해당 카테고리의 모든 채널을 불러옵니다.
        List<ChannelEntity> entities = channelRepository.findByCategoryIgnoreCase(category);

        // 2. 각 채널의 DTO 생성 (최근 영상 포함)
        List<ChannelDto> channelDtos = entities.stream()
                .map(entity -> {
                    List<VideoInfoDto> recentVideos = youTubeService.getRecentVideosCached(entity.getChannelId());
                    String thumbnail = youTubeService.getChannelThumbnailCached(entity.getChannelId());
                    return new ChannelDto(
                            entity.getCategory(),
                            entity.getChannelName(),
                            entity.getChannelLink(),
                            thumbnail,
                            entity.getChannelId(),
                            recentVideos
                    );
                })
                .collect(Collectors.toList());

        // 3. 최신 업로드 시간 기준으로 정렬 (최근 영상이 있는 경우 첫 번째 영상의 publishedAt 사용)
        channelDtos.sort((dto1, dto2) -> {
            LocalDateTime dt1 = (dto1.getRecentVideos() != null && !dto1.getRecentVideos().isEmpty())
                    ? dto1.getRecentVideos().get(0).getPublishedAt()
                    : LocalDateTime.MIN;
            LocalDateTime dt2 = (dto2.getRecentVideos() != null && !dto2.getRecentVideos().isEmpty())
                    ? dto2.getRecentVideos().get(0).getPublishedAt()
                    : LocalDateTime.MIN;
            return dt2.compareTo(dt1); // 내림차순 정렬
        });

        // 4. 정렬된 전체 목록에 대해 페이지네이션 적용
        return channelDtos.stream()
                .skip(page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    /**
     * 총 채널 수
     */
    public int getTotalCount(String category) {
        return (int) channelRepository.findByCategoryIgnoreCaseOrderByLatestUploadDesc(category, PageRequest.of(0, Integer.MAX_VALUE)).getTotalElements();
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
