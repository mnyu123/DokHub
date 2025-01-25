package com.DokHub.backend.service;

import com.DokHub.backend.dto.VideoInfoDto;
import com.DokHub.backend.dto.YouTubeChannelResponse;
import com.DokHub.backend.dto.YouTubeSearchResponse;
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
    private final String apiKey;
    private final RestTemplate restTemplate;

    public YouTubeService(RestTemplate restTemplate, @Value("${youtube.api.key:}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    @Cacheable(value = "channelThumbnailsBatch", key = "#channelIds")
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
            return response.getItems().stream().collect(Collectors.toMap(
                    YouTubeChannelResponse.Item::getId,
                    item -> item.getSnippet().getThumbnails().getDefaultThumbnail().getUrl()
            ));
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    @Cacheable(value = "youtubeVideos", key = "#channelId")
    public List<VideoInfoDto> getRecentVideos(String channelId) {
        if (channelId.isEmpty()) {
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
            return response.getItems().stream().map(item -> new VideoInfoDto(
                    item.getId().getVideoId(),
                    item.getSnippet().getTitle(),
                    LocalDateTime.parse(item.getSnippet().getPublishedAt(), DateTimeFormatter.ISO_DATE_TIME),
                    item.getSnippet().getThumbnails().getDefaultThumbnail().getUrl()
            )).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
