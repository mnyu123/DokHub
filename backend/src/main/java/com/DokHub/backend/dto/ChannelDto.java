package com.DokHub.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelDto {
    private String category;   // "clip(클립채널)", "song(노래방저장채널)", "main(독케익 본채널)" 등
    private String channelName; // 채널 이름
    private String channelLink; // 클립 채널 하이퍼링크
    private String videoPreviewUrl; // 유튜브 영상 하나 썸네일만 보여주는거
    // 필요 시 더 추가 (예: videoId, etc.)
    private String channelId; // YouTube 채널 ID
    private List<VideoInfoDto> recentVideos; // 분리된 DTO 사용
}

