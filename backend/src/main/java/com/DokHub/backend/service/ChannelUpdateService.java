package com.DokHub.backend.service;

import com.DokHub.backend.dto.VideoInfoDto;
import com.DokHub.backend.entity.ChannelEntity;
import com.DokHub.backend.repository.ChannelRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelUpdateService {

    private final ChannelRepository channelRepository;
    private final YouTubeService youTubeService;

    public ChannelUpdateService(ChannelRepository channelRepository, YouTubeService youTubeService) {
        this.channelRepository = channelRepository;
        this.youTubeService = youTubeService;
    }

    // 예를 들어, 매일 새벽에 최신 업로드 시간 업데이트
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateLatestUploadTimes() {
        List<ChannelEntity> channels = channelRepository.findAll();
        channels.forEach(channel -> {
            List<VideoInfoDto> videos = youTubeService.getRecentVideosCached(channel.getChannelId());
            if (videos != null && !videos.isEmpty()) {
                // 최근 영상이 정렬되어 있다고 가정할 때 첫 번째 영상의 업로드 시간을 사용
                channel.setLatestUpload(videos.get(0).getPublishedAt());
                channelRepository.save(channel);
            }
        });
        System.out.println("[DOKHUB] : 채널이 새로 업데이트 되었습니다.");
    }
}

