package com.DokHub.backend.service;

import com.DokHub.backend.dto.VideoInfoDto;
import com.DokHub.backend.dto.YouTubeChannelResponse;
import com.DokHub.backend.dto.YouTubeSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;     // ^^^ 추가
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled; // ^^^ 추가
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class YouTubeService {
    private final List<String> apiKeys;
    private final AtomicInteger currentKeyIndex = new AtomicInteger(0);
    private final RestTemplate restTemplate;

    // ^^^ 캐싱을 직접 관리하는 ConcurrentHashMap
    private final ConcurrentHashMap<String, List<VideoInfoDto>> recentVideosCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> thumbnailsCache = new ConcurrentHashMap<>();

    /**
     * 생성자에서는 properties 파일에 설정된 API 키 문자열(콤마 구분)을 받아 List로 변환합니다.
     */
    public YouTubeService(RestTemplate restTemplate,
                          @Value("${youtube.api.key}") String apiKeysStr) {
        this.restTemplate = restTemplate;
        // 콤마로 구분된 API 키 문자열을 List로 변환 (공백제거)
        this.apiKeys = Arrays.stream(apiKeysStr.split(","))
                .map(String::trim)
                .filter(key -> !key.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 현재 사용 중인 API 키를 반환
     */
    private String getCurrentApiKey() {
        return apiKeys.get(currentKeyIndex.get());
    }

    /**
     * API 호출 한도 초과 등으로 인해 API 키를 전환하는 메서드
     */
    private void switchToNextApiKey() {
        int nextIndex = (currentKeyIndex.get() + 1) % apiKeys.size();
        currentKeyIndex.set(nextIndex);
        System.out.println("[DOKHUB] : API 한도가 넘어 키를 교체합니다. " + getCurrentApiKey());
    }

    /**
     * 채널 썸네일을 배치로 가져오는 메서드
     * 캐싱(@Cacheable)과 동시에 로컬 캐시(thumbnailsCache)에도 저장
     */
    @Cacheable(value = "channelThumbnailsBatch", key = "#channelIds")
    public Map<String, String> getChannelThumbnailsBatch(List<String> channelIds) {
        if (channelIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String joinedIds = String.join(",", channelIds);
        String url = "https://www.googleapis.com/youtube/v3/channels"
                + "?part=snippet"
                + "&id=" + joinedIds
                + "&key=" + getCurrentApiKey();

        try {
            YouTubeChannelResponse response = restTemplate.getForObject(url, YouTubeChannelResponse.class);
            Map<String, String> resultMap = response.getItems().stream().collect(Collectors.toMap(
                    YouTubeChannelResponse.Item::getId,
                    item -> item.getSnippet().getThumbnails().getDefaultThumbnail().getUrl()
            ));
            // 로컬 캐시에 저장
            resultMap.forEach(thumbnailsCache::put);
            return resultMap;
        } catch (RestClientException e) {
            // 호출 한도 초과(quota) 등의 경우 메시지에 "quota"가 포함되어 있으면 API 키 전환 후 재시도
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("quota")) {
                switchToNextApiKey();
                try {
                    url = "https://www.googleapis.com/youtube/v3/channels"
                            + "?part=snippet"
                            + "&id=" + joinedIds
                            + "&key=" + getCurrentApiKey();
                    YouTubeChannelResponse response = restTemplate.getForObject(url, YouTubeChannelResponse.class);
                    Map<String, String> resultMap = response.getItems().stream().collect(Collectors.toMap(
                            YouTubeChannelResponse.Item::getId,
                            item -> item.getSnippet().getThumbnails().getDefaultThumbnail().getUrl()
                    ));
                    resultMap.forEach(thumbnailsCache::put);
                    return resultMap;
                } catch (Exception ex) {
                    return Collections.emptyMap();
                }
            }
            return Collections.emptyMap();
        }
    }

    /**
     * 단일 채널의 썸네일을 가져오며 로컬 캐시를 우선적으로 활용하는 편의 메서드
     */
    public String getChannelThumbnailCached(String channelId) {
        if (thumbnailsCache.containsKey(channelId)) {
            return thumbnailsCache.get(channelId);
        }
        Map<String, String> batchResult = getChannelThumbnailsBatch(Collections.singletonList(channelId));
        return batchResult.getOrDefault(channelId, "");
    }

    /**
     * 특정 채널의 최근 비디오 리스트를 가져오는 메서드
     * 캐싱(@Cacheable)과 동시에 로컬 캐시(recentVideosCache)에도 저장
     */
    @Cacheable(value = "youtubeVideos", key = "#channelId")
    public List<VideoInfoDto> getRecentVideos(String channelId) {
        if (channelId.isEmpty()) {
            return Collections.emptyList();
        }
        List<VideoInfoDto> videos = fetchRecentVideosFromApi(channelId);
        recentVideosCache.put(channelId, videos);
        return videos;
    }

    /**
     * 캐시된 최근 비디오 리스트를 우선적으로 반환하는 편의 메서드
     */
    public List<VideoInfoDto> getRecentVideosCached(String channelId) {
        if (recentVideosCache.containsKey(channelId)) {
            return recentVideosCache.get(channelId);
        }
        return getRecentVideos(channelId);
    }

    /**
     * 실제 YouTube API를 호출하여 최근 비디오 목록을 가져오는 내부 메서드
     */
    private List<VideoInfoDto> fetchRecentVideosFromApi(String channelId) {
        String url = "https://www.googleapis.com/youtube/v3/search"
                + "?part=snippet"
                + "&channelId=" + channelId
                + "&maxResults=3"
                + "&order=date"
                + "&type=video"
                + "&key=" + getCurrentApiKey();

        try {
            YouTubeSearchResponse response = restTemplate.getForObject(url, YouTubeSearchResponse.class);
            return response.getItems().stream().map(item -> new VideoInfoDto(
                    item.getId().getVideoId(),
                    item.getSnippet().getTitle(),
                    LocalDateTime.parse(item.getSnippet().getPublishedAt(), DateTimeFormatter.ISO_DATE_TIME),
                    item.getSnippet().getThumbnails().getDefaultThumbnail().getUrl()
            )).collect(Collectors.toList());
        } catch (RestClientException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("quota")) {
                switchToNextApiKey();
                try {
                    url = "https://www.googleapis.com/youtube/v3/search"
                            + "?part=snippet"
                            + "&channelId=" + channelId
                            + "&maxResults=3"
                            + "&order=date"
                            + "&type=video"
                            + "&key=" + getCurrentApiKey();
                    YouTubeSearchResponse response = restTemplate.getForObject(url, YouTubeSearchResponse.class);
                    return response.getItems().stream().map(item -> new VideoInfoDto(
                            item.getId().getVideoId(),
                            item.getSnippet().getTitle(),
                            LocalDateTime.parse(item.getSnippet().getPublishedAt(), DateTimeFormatter.ISO_DATE_TIME),
                            item.getSnippet().getThumbnails().getDefaultThumbnail().getUrl()
                    )).collect(Collectors.toList());
                } catch (Exception ex) {
                    return Collections.emptyList();
                }
            }
            return Collections.emptyList();
        }
    }

    /**
     * 주기적으로 전체 캐시를 갱신(무효화)하는 스케줄 메서드 (1시간마다 실행)
     */
    @Scheduled(fixedRate = 21600000) // 1시간 마다 실행
    @CacheEvict(value = {"youtubeVideos", "channelThumbnailsBatch"}, allEntries = true)
    public void refreshCache() {
        recentVideosCache.clear();
        thumbnailsCache.clear();
        System.out.println("[DOKHUB] : 6시간 스케줄러가 실행됩니다.");
    }

    /**
     * 특정 채널의 '최근 영상' 캐시를 강제로 비우는 메서드
     */
    @CacheEvict(value = "youtubeVideos", key = "#channelId")
    public void clearRecentVideosCache(String channelId) {
        recentVideosCache.remove(channelId);
        System.out.println("[DOKHUB] : 해당 채널의 최근영상 정보를 갱신합니다." + channelId);
    }

    /**
     * 특정 채널들의 썸네일 캐시를 강제로 비우는 메서드
     */
    @CacheEvict(value = "channelThumbnailsBatch", key = "#channelIds")
    public void clearThumbnailsCache(List<String> channelIds) {
        channelIds.forEach(thumbnailsCache::remove);
        System.out.println("[DOKHUB] : 해당 채널의 캐시를 강제로 비웠습니다. " + channelIds);
    }
}
