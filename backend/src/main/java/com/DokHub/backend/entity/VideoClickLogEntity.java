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
@Table(
        name = "video_click_log",
        schema = "dokhub",
        indexes = {
                @Index(name = "idx_video_click_log_video_id", columnList = "video_id"),
                @Index(name = "idx_video_click_log_clicked_at", columnList = "clicked_at"),
                @Index(name = "idx_video_click_log_category", columnList = "category")
        }
)
public class VideoClickLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "video_id", nullable = false, length = 32)
    private String videoId;

    @Column(name = "video_title", nullable = false, length = 500)
    private String videoTitle;

    @Column(name = "category", nullable = false, length = 30)
    private String category;

    @Column(name = "channel_name", length = 255)
    private String channelName;

    @Column(name = "channel_id", length = 64)
    private String channelId;

    @Column(name = "clicked_at", nullable = false)
    private LocalDateTime clickedAt;
}