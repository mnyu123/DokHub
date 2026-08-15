package com.DokHub.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "channels", schema = "dokhub") // 테이블과 매핑
public class ChannelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK (Auto-Increment)

    @Column(length = 255)
    private String category;         // clip, song, main

    @Column(name = "channel_name", length = 255)
    private String channelName;      // 채널 이름

    @Column(name = "channel_link", nullable = false, length = 255)
    private String channelLink;      // 채널 링크

    @Column(name = "video_preview_url", length = 255)
    private String videoPreviewUrl;  // 썸네일 URL

    @Column(name = "channel_id", unique = true, length = 255)
    private String channelId;        // YouTube 채널 ID

    // 최신 업로드 시간을 저장하는 컬럼 (2025.02.16 추가)
    @Column(name = "latest_upload")
    private LocalDateTime latestUpload;
}
