package com.DokHub.backend.service;

import com.DokHub.backend.dto.VideoInfoDto;
import com.DokHub.backend.dto.YouTubeChannelResponse;
import com.DokHub.backend.dto.YouTubeSearchResponse;
import com.DokHub.backend.dto.YouTubeVideosResponse;
import lombok.extern.slf4j.Slf4j;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
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
    public YouTubeService(RestTemplate restTemplate, @Value("${youtube.api.key}") String apiKeysStr) {
        this.restTemplate = restTemplate;
        // 콤마로 구분된 API 키 문자열을 List로 변환 (공백제거)
        this.apiKeys = Arrays.stream(apiKeysStr.split(",")).map(String::trim).filter(key -> !key.isEmpty()).collect(Collectors.toList());
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
     * - Channels.list API 호출 (최대 50개 ID 한 번에)
     * - @Cacheable 적용 (Spring Cache)
     * - 호출 성공 시 thumbnailsCache에도 저장
     * - quota 초과 시 API 키를 교체하고 1회 재시도
     */
    @Cacheable(value = "channelThumbnailsBatch", key = "#channelIds")
    public Map<String, String> getChannelThumbnailsBatch(List<String> channelIds) {
        if (channelIds == null || channelIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // ── 로그 추가: 현재 사용 중인 API 키 출력 ──
        String apiKey = getCurrentApiKey();
        log.info("[DOKHUB] 현재 사용 중인 YouTube API 키: {}", apiKey);

        // 1) ID 리스트를 쉼표로 결합
        String joinedIds = String.join(",", channelIds);
        String urlTemplate = "https://www.googleapis.com/youtube/v3/channels"
                + "?part=snippet"
                + "&id=" + joinedIds
                + "&key=%s";

        // 내부 함수: 실제 호출 + 캐싱
        Function<String, Map<String, String>> fetch = (key) -> {
            String url = String.format(urlTemplate, key);
            YouTubeChannelResponse resp = restTemplate.getForObject(url, YouTubeChannelResponse.class);
            if (resp == null || resp.getItems() == null) {
                return Collections.emptyMap();
            }
            Map<String, String> map = resp.getItems().stream().collect(Collectors.toMap(
                    YouTubeChannelResponse.Item::getId,
                    item -> item.getSnippet().getThumbnails().getDefaultThumbnail().getUrl()
            ));
            map.forEach(thumbnailsCache::put);
            return map;
        };

        // 2) 첫 시도: 현재 API 키로
        try {
            return fetch.apply(apiKey);
        } catch (RestClientException e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("quota")) {
                switchToNextApiKey();
                String nextKey = getCurrentApiKey();
                log.info("[DOKHUB] quota 초과, 교체된 YouTube API 키: {}", nextKey);
                try {
                    return fetch.apply(nextKey);
                } catch (Exception ex) {
                    log.warn("[DOKHUB] 채널 썸네일 배치 조회 재시도 실패: {}", ex.getMessage());
                    return Collections.emptyMap();
                }
            }
            log.error("[DOKHUB] 채널 썸네일 배치 조회 실패: {}", e.getMessage());
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
        // ① Search API로 최근 3개 videoId 가져오기
        String searchUrl = "https://www.googleapis.com/youtube/v3/search" + "?part=snippet" + "&channelId=" + channelId + "&maxResults=3" + "&order=date" + "&type=video" + "&key=" + getCurrentApiKey();

        YouTubeSearchResponse searchRes = restTemplate.getForObject(searchUrl, YouTubeSearchResponse.class);
        if (searchRes == null || searchRes.getItems() == null || searchRes.getItems().isEmpty()) {
            return Collections.emptyList();
        }

        // videoId 목록 쉼표 연결
        String joinedIds = searchRes.getItems().stream().map(item -> item.getId().getVideoId()).collect(Collectors.joining(","));

        // ② videos.list(status) 호출해 상태 확인
        String videosUrl = "https://www.googleapis.com/youtube/v3/videos" + "?part=status,snippet" + "&id=" + joinedIds + "&key=" + getCurrentApiKey();

        YouTubeVideosResponse videosRes = restTemplate.getForObject(videosUrl, YouTubeVideosResponse.class);
        if (videosRes == null || videosRes.getItems() == null) {
            return Collections.emptyList();
        }

        // id → status 매핑
        Map<String, YouTubeVideosResponse.Item.Status> statusMap = videosRes.getItems().stream().collect(Collectors.toMap(YouTubeVideosResponse.Item::getId, YouTubeVideosResponse.Item::getStatus));

        // 필요하면 썸네일 고화질로 교체
        Map<String, String> highThumbMap = videosRes.getItems().stream().collect(Collectors.toMap(YouTubeVideosResponse.Item::getId, it -> Optional.ofNullable(it.getSnippet()).map(YouTubeVideosResponse.Item.Snippet::getThumbnails).map(thumbs -> Optional.ofNullable(thumbs.getHigh()).orElse(thumbs.getMedium())).map(YouTubeVideosResponse.Item.Snippet.Thumbnails.Thumbnail::getUrl).orElse("")));

        // ③ 조건 필터링 후 DTO 변환
        return searchRes.getItems().stream().filter(item -> {
            YouTubeVideosResponse.Item.Status st = statusMap.get(item.getId().getVideoId());
            return st != null && "public".equals(st.getPrivacyStatus()) && "processed".equals(st.getUploadStatus()) && Boolean.TRUE.equals(st.getEmbeddable());
        }).map(item -> {
            String vid = item.getId().getVideoId();
            String thumb = highThumbMap.getOrDefault(vid, item.getSnippet().getThumbnails().getDefaultThumbnail().getUrl());
            return new VideoInfoDto(vid, item.getSnippet().getTitle(), LocalDateTime.parse(item.getSnippet().getPublishedAt(), DateTimeFormatter.ISO_DATE_TIME), thumb);
        }).collect(Collectors.toList());
    }


    // ■ 캐시 강제 초기화 스케줄: 6시간 → 하루 1회(자정)로 완화
    @Scheduled(cron = "0 0 0 * * ?")  // 매일 00:00 실행
    @CacheEvict(value = {"youtubeVideos", "channelThumbnailsBatch"}, allEntries = true)
    public void refreshCache() {
        recentVideosCache.clear();
        thumbnailsCache.clear();
        System.out.println("[DOKHUB] : 일일 캐시 초기화 완료");
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
