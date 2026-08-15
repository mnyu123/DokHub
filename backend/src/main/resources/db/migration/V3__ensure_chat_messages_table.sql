CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGINT NOT NULL AUTO_INCREMENT,
    content VARCHAR(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    message_time BIGINT DEFAULT NULL,
    sender_channel_id VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_chat_messages_message_time (message_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
