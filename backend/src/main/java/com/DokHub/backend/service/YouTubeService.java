package com.DokHub.backend.service;

import com.DokHub.backend.dto.VideoInfoDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class YouTubeService {
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

    // 1. 채널의 최신 영상 3개 가져오기
    @Cacheable(value = "youtubeVideos", key = "#channelId")
    public List<VideoInfoDto> getRecentVideos(String channelId) {
        String url = "https://www.googleapis.com/youtube/v3/search"
                + "?part=snippet"
                + "&channelId=" + channelId
                + "&maxResults=3"
                + "&order=date"
                + "&type=video"
                + "&key=" + apiKey;

        try {
            YouTubeSearchResponse response = restTemplate.getForObject(url, YouTubeSearchResponse.class);
            return response.getItems().stream()
                    .map(item -> new VideoInfoDto(
                            item.getId().getVideoId(),
                            item.getSnippet().getTitle(),
                            LocalDateTime.parse(item.getSnippet().getPublishedAt(), DateTimeFormatter.ISO_DATE_TIME),
                            item.getSnippet().getThumbnails().getDefaultThumbnail().getUrl()
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // 2. 채널 썸네일 가져오기
    @Cacheable(value = "channelThumbnails", key = "#channelId") // API 요청횟수 넘많아서 캐싱적용
    public String getChannelThumbnail(String channelId) {
        String url = "https://www.googleapis.com/youtube/v3/channels"
                + "?part=snippet"
                + "&id=" + channelId
                + "&key=" + apiKey;

        try {
            YouTubeChannelResponse response = restTemplate.getForObject(url, YouTubeChannelResponse.class);
            return response.getItems().get(0).getSnippet().getThumbnails().getDefaultThumbnail().getUrl();
        } catch (Exception e) {
            return "https://example.com/default_thumbnail.jpg"; // 기본 썸네일 URL
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