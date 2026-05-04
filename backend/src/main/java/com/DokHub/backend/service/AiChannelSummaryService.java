package com.DokHub.backend.service;

import com.DokHub.backend.dto.AiChannelSummaryResponse;
import com.DokHub.backend.dto.VideoInfoDto;
import com.DokHub.backend.entity.ChannelEntity;
import com.DokHub.backend.repository.ChannelRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AiChannelSummaryService {

    private final ChannelRepository channelRepository;
    private final YouTubeService youTubeService;

    public AiChannelSummaryService(ChannelRepository channelRepository, YouTubeService youTubeService) {
        this.channelRepository = channelRepository;
        this.youTubeService = youTubeService;
    }

    public AiChannelSummaryResponse buildSummary(String category, int periodDays) {
        int safePeriodDays = Math.max(1, Math.min(periodDays, 30));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusDays(safePeriodDays);

        List<ChannelEntity> channels = channelRepository.findByCategoryIgnoreCase(category);
        List<ChannelStat> channelStats = new ArrayList<>();

        for (ChannelEntity channel : channels) {
            List<VideoInfoDto> recentVideos = youTubeService.getRecentVideosCached(channel.getChannelId());
            if (recentVideos == null) {
                recentVideos = new ArrayList<>();
            }

            List<VideoInfoDto> inPeriodVideos = recentVideos.stream()
                    .filter(Objects::nonNull)
                    .filter(video -> video.getPublishedAt() != null)
                    .filter(video -> !video.getPublishedAt().isBefore(from))
                    .toList();

            LocalDateTime lastUploadAt = recentVideos.stream()
                    .filter(Objects::nonNull)
                    .map(VideoInfoDto::getPublishedAt)
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            long daysSinceLastUpload = lastUploadAt == null
                    ? -1
                    : Math.max(0, Duration.between(lastUploadAt, now).toDays());

            channelStats.add(new ChannelStat(
                    channel.getChannelName(),
                    inPeriodVideos.size(),
                    lastUploadAt,
                    daysSinceLastUpload
            ));
        }

        int totalChannels = channelStats.size();
        int activeChannels = (int) channelStats.stream()
                .filter(stat -> stat.videoCount > 0)
                .count();
        int inactiveChannels = totalChannels - activeChannels;
        int totalVideos = channelStats.stream()
                .mapToInt(stat -> stat.videoCount)
                .sum();

        AiChannelSummaryResponse.Stats stats = new AiChannelSummaryResponse.Stats(
                totalChannels,
                activeChannels,
                inactiveChannels,
                totalVideos,
                from.toLocalDate().toString(),
                now.toLocalDate().toString()
        );

        List<AiChannelSummaryResponse.ChannelActivity> topActiveChannels = channelStats.stream()
                .filter(stat -> stat.videoCount > 0)
                .sorted(Comparator
                        .comparingInt((ChannelStat stat) -> stat.videoCount).reversed()
                        .thenComparing((ChannelStat stat) -> stat.lastUploadAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(3)
                .map(stat -> new AiChannelSummaryResponse.ChannelActivity(
                        stat.channelName,
                        stat.videoCount,
                        stat.lastUploadAt == null ? null : stat.lastUploadAt.toString(),
                        stat.daysSinceLastUpload
                ))
                .collect(Collectors.toList());

        List<AiChannelSummaryResponse.ChannelActivity> recentlyInactiveChannels = channelStats.stream()
                .filter(stat -> stat.videoCount == 0)
                .filter(stat -> stat.daysSinceLastUpload >= 0)
                .sorted(Comparator
                        .comparingLong((ChannelStat stat) -> stat.daysSinceLastUpload).reversed()
                        .thenComparing(stat -> stat.channelName))
                .limit(3)
                .map(stat -> new AiChannelSummaryResponse.ChannelActivity(
                        stat.channelName,
                        stat.videoCount,
                        stat.lastUploadAt == null ? null : stat.lastUploadAt.toString(),
                        stat.daysSinceLastUpload
                ))
                .collect(Collectors.toList());

        String overallSummary = buildSimpleSummary(
                category,
                safePeriodDays,
                totalChannels,
                activeChannels,
                inactiveChannels,
                totalVideos,
                topActiveChannels,
                recentlyInactiveChannels
        );

        return new AiChannelSummaryResponse(
                category,
                safePeriodDays,
                overallSummary,
                stats,
                topActiveChannels,
                recentlyInactiveChannels
        );
    }

    private String buildSimpleSummary(
            String category,
            int periodDays,
            int totalChannels,
            int activeChannels,
            int inactiveChannels,
            int totalVideos,
            List<AiChannelSummaryResponse.ChannelActivity> topActiveChannels,
            List<AiChannelSummaryResponse.ChannelActivity> recentlyInactiveChannels
    ) {
        StringBuilder sb = new StringBuilder();

        sb.append("최근 ")
                .append(periodDays)
                .append("일 동안 ")
                .append(totalChannels)
                .append("개 ")
                .append(categoryLabel(category))
                .append(" 채널에서 총 ")
                .append(totalVideos)
                .append("개의 영상이 확인되었습니다. ");

        sb.append("이 기간에 활동이 있었던 채널은 ")
                .append(activeChannels)
                .append("개이고, 업로드가 없었던 채널은 ")
                .append(inactiveChannels)
                .append("개입니다.");

        if (!topActiveChannels.isEmpty()) {
            AiChannelSummaryResponse.ChannelActivity top = topActiveChannels.get(0);
            sb.append(" 가장 활발한 채널은 ")
                    .append(top.getChannelName())
                    .append("으로, 최근 ")
                    .append(periodDays)
                    .append("일 기준 ")
                    .append(top.getVideoCount())
                    .append("개의 업로드가 있었습니다.");
        }

        if (!recentlyInactiveChannels.isEmpty()) {
            AiChannelSummaryResponse.ChannelActivity inactive = recentlyInactiveChannels.get(0);
            if (inactive.getDaysSinceLastUpload() >= 0) {
                sb.append(" 반대로 ")
                        .append(inactive.getChannelName())
                        .append(" 채널은 마지막 업로드 후 ")
                        .append(inactive.getDaysSinceLastUpload())
                        .append("일 정도 지난 상태입니다.");
            }
        }

        return sb.toString();
    }

    private String categoryLabel(String category) {
        if (category == null) {
            return "영상";
        }

        switch (category.toLowerCase()) {
            case "clip":
                return "클립";
            case "replay":
                return "다시보기";
            case "song":
                return "노래";
            case "main":
                return "본채널";
            case "stclip":
                return "중단 클립";
            default:
                return category;
        }
    }

    private static class ChannelStat {
        private final String channelName;
        private final int videoCount;
        private final LocalDateTime lastUploadAt;
        private final long daysSinceLastUpload;

        private ChannelStat(String channelName, int videoCount, LocalDateTime lastUploadAt, long daysSinceLastUpload) {
            this.channelName = channelName;
            this.videoCount = videoCount;
            this.lastUploadAt = lastUploadAt;
            this.daysSinceLastUpload = daysSinceLastUpload;
        }
    }
}