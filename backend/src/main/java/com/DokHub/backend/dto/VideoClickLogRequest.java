package com.DokHub.backend.dto;

import lombok.Data;

@Data
public class VideoClickLogRequest {
    private String videoId;
    private String videoTitle;
    private String category;
    private String channelName;
    private String channelId;
}