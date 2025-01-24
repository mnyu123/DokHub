package com.DokHub.backend.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelDto {
    private String category;           // "clip(클립채널)", "song(노래방저장채널)", "main(독케익 본채널)" 등
    private String channelName;       // 채널 이름
    private String channelLink;       // 클립 채널 하이퍼링크
    private String thumbnailUrl;      // 추가된 썸네일 URL 프로퍼티
    private String channelId;         // YouTube 채널 ID
    private List<VideoInfoDto> recentVideos; // 최근 영상 목록
}

