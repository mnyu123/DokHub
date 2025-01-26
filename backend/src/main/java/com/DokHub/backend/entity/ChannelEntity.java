package com.DokHub.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
