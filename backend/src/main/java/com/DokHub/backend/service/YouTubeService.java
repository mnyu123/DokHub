package com.DokHub.backend.service;

import com.DokHub.backend.dto.VideoInfoDto;
import com.DokHub.backend.dto.YouTubeChannelResponse;
import com.DokHub.backend.dto.YouTubeSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;     // ^^^ 추가
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled; // ^^^ 추가
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class YouTubeService {
    private final String apiKey;
    private final RestTemplate restTemplate;

    // ^^^ 캐싱을 직접 관리하는 ConcurrentHashMap
    private final ConcurrentHashMap<String, List<VideoInfoDto>> recentVideosCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> thumbnailsCache = new ConcurrentHashMap<>();

    public YouTubeService(RestTemplate restTemplate, @Value("${youtube.api.key:}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    /**
     * 채널 썸네일을 "배치"로 가져오는 메서드
     * -> 캐싱(@Cacheable) + 동시에 thumbnailsCache에 반영
     */
    @Cacheable(value = "channelThumbnailsBatch", key = "#channelIds") // 스프링 캐시
    public Map<String, String> getChannelThumbnailsBatch(List<String> channelIds) {
        if (channelIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String joinedIds = String.join(",", channelIds);
        String url = "https://www.googleapis.com/youtube/v3/channels"
                + "?part=snippet"
                + "&id=" + joinedIds
                + "&key=" + apiKey;

        try {
            YouTubeChannelResponse response = restTemplate.getForObject(url, YouTubeChannelResponse.class);
            Map<String, String> resultMap = response.getItems().stream().collect(Collectors.toMap(
                    YouTubeChannelResponse.Item::getId,
                    item -> item.getSnippet().getThumbnails().getDefaultThumbnail().getUrl()
            ));

            // ^^^ thumbnailsCache에 직접 저장
            for (Map.Entry<String, String> entry : resultMap.entrySet()) {
                thumbnailsCache.put(entry.getKey(), entry.getValue());
            }

            return resultMap;
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    /**
     * "단일" 채널 ID로 썸네일을 가져와서 thumbnailsCache를 갱신하는 편의 메서드
     * ^^^ (직접 사용 예시를 위해 추가)
     */
    public String getChannelThumbnailCached(String channelId) {
        // 먼저 캐시에 있으면 꺼내 사용
        if (thumbnailsCache.containsKey(channelId)) {
            return thumbnailsCache.get(channelId);
        }
        // 없으면 배치 메서드 재활용
        Map<String, String> batchResult = getChannelThumbnailsBatch(Collections.singletonList(channelId));
        return batchResult.getOrDefault(channelId, "");
    }

    /**
     * 특정 채널의 최근 비디오 리스트를 가져오는 메서드
     * -> 캐싱(@Cacheable) + 동시에 recentVideosCache에 반영
     */
    @Cacheable(value = "youtubeVideos", key = "#channelId")
    public List<VideoInfoDto> getRecentVideos(String channelId) {
        if (channelId.isEmpty()) {
            return Collections.emptyList();
        }
        List<VideoInfoDto> videos = fetchRecentVideosFromApi(channelId);
        // ^^^ 가져온 데이터를 ConcurrentHashMap에도 저장
        recentVideosCache.put(channelId, videos);
        return videos;
    }

    /**
     * 단순 편의 메서드: 직접 ConcurrentHashMap을 먼저 확인하는 캐시 버전
     * ^^^ (직접 사용 예시를 위해 추가)
     */
    public List<VideoInfoDto> getRecentVideosCached(String channelId) {
        // ^^^ local 캐시(ConcurrentHashMap)에 먼저 존재하면 반환
        if (recentVideosCache.containsKey(channelId)) {
            return recentVideosCache.get(channelId);
        }
        // ^^^ 없다면, 원래 메서드(getRecentVideos)를 호출
        return getRecentVideos(channelId);
    }

    /**
     * 실제 YouTube API를 호출하여 영상 목록을 가져오는 내부 메서드
     */
    private List<VideoInfoDto> fetchRecentVideosFromApi(String channelId) {
        String url = "https://www.googleapis.com/youtube/v3/search"
                + "?part=snippet"
                + "&channelId=" + channelId
                + "&maxResults=3"
                + "&order=date"
                + "&type=video"
                + "&key=" + apiKey;

        try {
            YouTubeSearchResponse response = restTemplate.getForObject(url, YouTubeSearchResponse.class);
            return response.getItems().stream().map(item -> new VideoInfoDto(
                    item.getId().getVideoId(),
                    item.getSnippet().getTitle(),
                    LocalDateTime.parse(item.getSnippet().getPublishedAt(), DateTimeFormatter.ISO_DATE_TIME),
                    item.getSnippet().getThumbnails().getDefaultThumbnail().getUrl()
                    // item.getSnippet().getThumbnails().getHighThumbnail().getUrl() // 고화질 썸네일 적용
            )).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * 주기적으로 "전체 캐시"를 갱신 (실제로는 캐시 전체 무효화)하는 스케줄 메서드
     * ^^^ (refreshCache 메서드가 사용되지 않는 경고를 없애기 위해)
     */
    @Scheduled(fixedRate = 3600000) // 1시간마다 실행
    @CacheEvict(value = {"youtubeVideos", "channelThumbnailsBatch"}, allEntries = true)
    public void refreshCache() {
        // ^^^ 스프링 캐시에 저장된 내용은 삭제
        // ^^^ 아래는 local 캐시(ConcurrentHashMap) 초기화
        recentVideosCache.clear();
        thumbnailsCache.clear();
        System.out.println("[Scheduled] YouTubeService cache is refreshed.");
    }

    /**
     * 특정 채널의 '최근 영상' 캐시를 강제로 비우는 메서드
     */
    @CacheEvict(value = "youtubeVideos", key = "#channelId")
    public void clearRecentVideosCache(String channelId) {
        recentVideosCache.remove(channelId);
        System.out.println("Cleared recent videos cache for channelId: " + channelId);
    }

    /**
     * 특정 채널들의 썸네일 캐시를 강제로 비우는 메서드
     */
    @CacheEvict(value = "channelThumbnailsBatch", key = "#channelIds")
    public void clearThumbnailsCache(List<String> channelIds) {
        channelIds.forEach(thumbnailsCache::remove);
        System.out.println("Cleared thumbnails cache for channelIds: " + channelIds);
    }
}
