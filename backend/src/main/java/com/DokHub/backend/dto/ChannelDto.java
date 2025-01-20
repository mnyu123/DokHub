package com.DokHub.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelDto {
    private String category;   // "clip", "song", "main" 등
    private String channelName;
    private String channelLink;
    private String videoPreviewUrl; // 영상 미리보기 Thumbnail 또는 Embeded url
    // 필요 시 더 추가 (예: videoId, etc.)
}

