package com.DokHub.backend.service;

import com.DokHub.backend.dto.VideoClickLogRequest;
import com.DokHub.backend.entity.VideoClickLogEntity;
import com.DokHub.backend.repository.VideoClickLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class VideoClickLogService {

    private final VideoClickLogRepository videoClickLogRepository;

    public VideoClickLogService(VideoClickLogRepository videoClickLogRepository) {
        this.videoClickLogRepository = videoClickLogRepository;
    }

    @Transactional
    public void saveClick(VideoClickLogRequest request) {
        if (request == null) {
            return;
        }

        if (isBlank(request.getVideoId()) || isBlank(request.getVideoTitle()) || isBlank(request.getCategory())) {
            return;
        }

        VideoClickLogEntity entity = new VideoClickLogEntity();
        entity.setVideoId(trimToLength(request.getVideoId(), 32));
        entity.setVideoTitle(trimToLength(request.getVideoTitle(), 500));
        entity.setCategory(trimToLength(request.getCategory(), 30));
        entity.setChannelName(trimToLength(request.getChannelName(), 255));
        entity.setChannelId(trimToLength(request.getChannelId(), 64));
        entity.setClickedAt(LocalDateTime.now());

        videoClickLogRepository.save(entity);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String trimToLength(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() <= maxLength) {
            return trimmed;
        }
        return trimmed.substring(0, maxLength);
    }
}