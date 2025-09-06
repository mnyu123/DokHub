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
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    // 2025-09-06 : 플레이리스트 관리용 map 추가
    private final ConcurrentHashMap<String, List<VideoInfoDto>> playlistItemsCache = new ConcurrentHashMap<>();

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
        // 빈 키 방어
        if (this.apiKeys.isEmpty()) {
            throw new IllegalStateException("[DOKHUB] youtube.api.key 가 설정되지 않았습니다. 최소 1개 이상 등록하세요.");
        }
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

    // RFC3339(Z, +09:00 등) 안전 파싱
    private LocalDateTime parsePublishedAt(String s) {
        if (s == null || s.isBlank()) return LocalDateTime.MIN;
        try {
            return OffsetDateTime.parse(s).toLocalDateTime();
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(s);
            } catch (DateTimeParseException ignored) {
                return LocalDateTime.MIN;
            }
        }
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

            /* 기존코드 주석처리
            Map<String, String> resultMap = response.getItems().stream().collect(Collectors.toMap(
                    YouTubeChannelResponse.Item::getId,
                    item -> item.getSnippet().getThumbnails().getDefaultThumbnail().getUrl()
            ));
            */

            if (response == null || response.getItems() == null) return Collections.emptyMap();
            Map<String, String> resultMap = response.getItems().stream().collect(Collectors.toMap(
                    YouTubeChannelResponse.Item::getId,
                    item -> {
                        if (item.getSnippet() == null || item.getSnippet().getThumbnails() == null || item.getSnippet().getThumbnails().getDefaultThumbnail() == null) {
                            return "";
                        }
                        return item.getSnippet().getThumbnails().getDefaultThumbnail().getUrl();
                    }
            ));

            // 로컬 캐시에 저장
            thumbnailsCache.putAll(resultMap);
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
                    thumbnailsCache.putAll(resultMap);
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
        if (channelId == null || channelId.isEmpty()) {
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
        if (channelId == null || channelId.isEmpty()) {
            return Collections.emptyList();
        }
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
     * 재생목록 아이템(최대 50) 캐시 우선 반환
     */
    public List<VideoInfoDto> getPlaylistItemsCached(String playlistId, int maxResults) {
        String key = playlistId + ":" + maxResults;
        if (playlistItemsCache.containsKey(key)) {
            return playlistItemsCache.get(key);
        }
        List<VideoInfoDto> items = getPlaylistItems(playlistId, maxResults);
        playlistItemsCache.put(key, items);
        return items;
    }

    /**
     * 유튜브 재생목록 전용으로 만든 서비스
     */
    @Cacheable(value = "youtubePlaylistItems", key = "#playlistId + ':' + #maxResults")
    public List<VideoInfoDto> getPlaylistItems(String playlistId, int maxResults) {
        if (playlistId == null || playlistId.isBlank()) return Collections.emptyList();
        int clamped = Math.max(1, Math.min(maxResults, 50));
        String url = "https://www.googleapis.com/youtube/v3/playlistItems"
                + "?part=snippet"
                + "&playlistId=" + playlistId
                + "&maxResults=" + clamped
                + "&key=" + getCurrentApiKey();
        try {
            com.DokHub.backend.dto.YouTubePlaylistItemsResponse resp =
                    restTemplate.getForObject(url, com.DokHub.backend.dto.YouTubePlaylistItemsResponse.class);
            if (resp == null || resp.getItems() == null) return Collections.emptyList();
            return resp.getItems().stream()
                    .map(it -> {
                        var sn = it.getSnippet();
                        String vid = (sn != null && sn.getResourceId() != null) ? sn.getResourceId().getVideoId() : null;
                        String title = (sn != null) ? sn.getTitle() : null;
                        String thumb = (sn != null && sn.getThumbnails() != null && sn.getThumbnails().getDefaultThumbnail() != null)
                                ? sn.getThumbnails().getDefaultThumbnail().getUrl() : "";
                        return new VideoInfoDto(
                                vid,
                                title,
                                parsePublishedAt(sn != null ? sn.getPublishedAt() : null),
                                thumb
                        );
                    })
                    .filter(v -> v.getVideoId() != null && !v.getVideoId().isBlank())
                    .collect(Collectors.toList());
        } catch (org.springframework.web.client.RestClientException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("quota")) {
                switchToNextApiKey();
                try {
                    url = "https://www.googleapis.com/youtube/v3/playlistItems"
                            + "?part=snippet"
                            + "&playlistId=" + playlistId
                            + "&maxResults=" + clamped
                            + "&key=" + getCurrentApiKey();
                    var resp = restTemplate.getForObject(url, com.DokHub.backend.dto.YouTubePlaylistItemsResponse.class);
                    if (resp == null || resp.getItems() == null) return Collections.emptyList();
                    return resp.getItems().stream()
                            .map(it -> {
                                var sn = it.getSnippet();
                                String vid = (sn != null && sn.getResourceId() != null) ? sn.getResourceId().getVideoId() : null;
                                String title = (sn != null) ? sn.getTitle() : null;
                                String thumb = (sn != null && sn.getThumbnails() != null && sn.getThumbnails().getDefaultThumbnail() != null)
                                        ? sn.getThumbnails().getDefaultThumbnail().getUrl() : "";
                                return new VideoInfoDto(
                                        vid,
                                        title,
                                        parsePublishedAt(sn != null ? sn.getPublishedAt() : null),
                                        thumb
                                );
                            })
                            .filter(v -> v.getVideoId() != null && !v.getVideoId().isBlank())
                            .collect(Collectors.toList());
                } catch (Exception ignore) {
                    return Collections.emptyList();
                }
            }
            return Collections.emptyList();
        }
    }

    /**
     * 주기적으로 전체 캐시를 갱신(무효화)하는 스케줄 메서드 (6시간마다 실행)
     */
    @Scheduled(fixedRate = 21600000) // 6시간 마다 실행
    //@CacheEvict(value = {"youtubeVideos", "channelThumbnailsBatch"}, allEntries = true)
    @CacheEvict(value = {"youtubeVideos", "channelThumbnailsBatch", "youtubePlaylistItems"}, allEntries = true)
    public void refreshCache() {
        recentVideosCache.clear();
        thumbnailsCache.clear();
        playlistItemsCache.clear(); // 재생목록 캐시 비우기 추가
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
