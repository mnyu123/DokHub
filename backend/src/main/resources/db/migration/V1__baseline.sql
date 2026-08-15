CREATE TABLE IF NOT EXISTS video_click_log (
    id BIGINT NOT NULL AUTO_INCREMENT,
    category VARCHAR(30) COLLATE utf8mb4_unicode_ci NOT NULL,
    channel_id VARCHAR(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    channel_name VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    clicked_at DATETIME(6) NOT NULL,
    video_id VARCHAR(32) COLLATE utf8mb4_unicode_ci NOT NULL,
    video_title VARCHAR(500) COLLATE utf8mb4_unicode_ci NOT NULL,
    PRIMARY KEY (id),
    KEY idx_video_click_log_video_id (video_id),
    KEY idx_video_click_log_clicked_at (clicked_at),
    KEY idx_video_click_log_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGINT NOT NULL AUTO_INCREMENT,
    content VARCHAR(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    message_time BIGINT DEFAULT NULL,
    sender_channel_id VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS channels (
    id BIGINT NOT NULL AUTO_INCREMENT,
    category VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    channel_name VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    channel_link VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    video_preview_url VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    channel_id VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    latest_upload DATETIME DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY channel_id (channel_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
