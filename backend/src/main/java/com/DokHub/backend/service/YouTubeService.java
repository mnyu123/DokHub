package com.DokHub.backend.service;

import com.DokHub.backend.dto.VideoInfoDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class YouTubeService {
    @Value("${youtube.api.key}")
    private String apiKey;
    private final RestTemplate restTemplate;

    public YouTubeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        // .env 파일 경로 명시적 지정
        Dotenv dotenv = Dotenv.configure()
                .directory("src/main/resources")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        this.apiKey = dotenv.get("YOUTUBE_API_KEY");
        // System.out.println("Loaded API Key: " + this.apiKey); // 로깅 추가

        if (this.apiKey == null || this.apiKey.isEmpty()) {
            throw new IllegalStateException("YOUTUBE_API_KEY가 설정되지 않았습니다.");
        }
    }

    /**
     * 여러 채널의 썸네일을 한 번에 가져오는 메서드
     */
    @Cacheable(value = "channelThumbnailsBatch", key = "#channelIds")
    public Map<String, String> getChannelThumbnailsBatch(List<String> channelIds) {
        if (channelIds == null || channelIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String joinedIds = String.join(",", channelIds);
        String url = "https://www.googleapis.com/youtube/v3/channels"
                + "?part=snippet"
                + "&id=" + joinedIds
                + "&key=" + apiKey;

        try {
            YouTubeChannelResponse response = restTemplate.getForObject(url, YouTubeChannelResponse.class);
            Map<String, String> thumbnails = new HashMap<>();
            for (YouTubeChannelResponse.Item item : response.getItems()) {
                thumbnails.put(item.getId(), item.getSnippet().getThumbnails().getDefaultThumbnail().getUrl());
            }
            return thumbnails;
        } catch (Exception e) {
            // 예외 발생 시 모든 채널에 대해 기본 썸네일을 반환
            return channelIds.stream().collect(Collectors.toMap(
                    id -> id,
                    id -> "https://dokhub-backend2.fly.dev/api/channels/default_thumbnail.png"
            ));
        }
    }

    /**
     * 특정 채널의 최근 영상 3개를 가져오는 메서드
     */
    @Cacheable(value = "youtubeVideos", key = "#channelId")
    public List<VideoInfoDto> getRecentVideos(String channelId) {
        if (channelId == null || channelId.isEmpty()) {
            return Collections.emptyList();
        }

        String url = "https://www.googleapis.com/youtube/v3/search"
                + "?part=snippet"
                + "&channelId=" + channelId
                + "&maxResults=3"
                + "&order=date"
                + "&type=video"
                + "&key=" + apiKey;

        try {
            YouTubeSearchResponse response = restTemplate.getForObject(url, YouTubeSearchResponse.class);
            if (response == null || response.getItems() == null) {
                return Collections.emptyList();
            }

            return response.getItems().stream()
                    .map(item -> {
                        try {
                            return new VideoInfoDto(
                                    item.getId().getVideoId(),
                                    item.getSnippet().getTitle(),
                                    LocalDateTime.parse(item.getSnippet().getPublishedAt(), DateTimeFormatter.ISO_DATE_TIME),
                                    item.getSnippet().getThumbnails().getDefaultThumbnail().getUrl()
                            );
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // 로깅 추가
            System.err.println("YouTube API 호출 실패: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // ===== YouTube API 응답 DTO =====

    // 검색 결과 (최신 영상)
    @Data
    public static class YouTubeSearchResponse {
        private List<Item> items;

        @Data
        public static class Item {
            private Id id;
            private Snippet snippet;

            @Data
            public static class Id {
                private String videoId;
            }

            @Data
            public static class Snippet {
                private String title;
                private String publishedAt;
                private Thumbnails thumbnails;

                @Data
                public static class Thumbnails {
                    @JsonProperty("default")
                    private Thumbnail defaultThumbnail;

                    public Thumbnail getDefaultThumbnail() {
                        return this.defaultThumbnail;
                    }

                    @Data
                    public static class Thumbnail {
                        private String url;
                    }
                }
            }
        }
    }

    // 채널 정보 (썸네일)
    @Data
    public static class YouTubeChannelResponse {
        private List<Item> items;

        @Data
        public static class Item {
            private String id;
            private Snippet snippet;

            @Data
            public static class Snippet {
                private Thumbnails thumbnails;

                @Data
                public static class Thumbnails {
                    @JsonProperty("default")
                    private Thumbnail defaultThumbnail;

                    public Thumbnail getDefaultThumbnail() {
                        return this.defaultThumbnail;
                    }

                    @Data
                    public static class Thumbnail {
                        private String url;
                    }
                }
            }
        }
    }
}