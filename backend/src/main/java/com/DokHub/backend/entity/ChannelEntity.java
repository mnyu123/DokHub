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

    private String category;         // clip, song, main
    private String channelName;      // 채널 이름
    private String channelLink;      // 채널 링크
    private String videoPreviewUrl;  // 썸네일 URL
    private String channelId;        // YouTube 채널 ID

    // 최신 업로드 시간을 저장하는 컬럼 (2025.02.16 추가)
    @Column(name = "latest_upload")
    private LocalDateTime latestUpload;
}
