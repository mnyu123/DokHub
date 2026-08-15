CREATE INDEX idx_video_click_log_clicked_video
    ON video_click_log (clicked_at, video_id);

CREATE INDEX idx_video_click_log_clicked_channel
    ON video_click_log (clicked_at, channel_id);
