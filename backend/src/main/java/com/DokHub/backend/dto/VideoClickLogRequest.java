package com.DokHub.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VideoClickLogRequest {
    @NotBlank
    @Size(max = 32)
    private String videoId;

    @NotBlank
    @Size(max = 500)
    private String videoTitle;

    @NotBlank
    @Size(max = 30)
    private String category;

    @Size(max = 255)
    private String channelName;

    @Size(max = 64)
    private String channelId;
}
