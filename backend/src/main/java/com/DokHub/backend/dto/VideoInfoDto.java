package com.DokHub.backend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class VideoInfoDto { // 유튜브 API 사용하는 DTO
    private String videoId;
    private String videoTitle;
    private LocalDateTime publishedAt;
    private String thumbnailUrl;
}
