package com.DokHub.backend.service;

import com.DokHub.backend.dto.ClickAnalyticsResponse;
import com.DokHub.backend.repository.VideoClickLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ClickAnalyticsAgentService {

    private static final int DEFAULT_PERIOD_DAYS = 30;
    private static final int DEFAULT_LIMIT = 5;

    private final VideoClickLogRepository videoClickLogRepository;
    private final Map<SnapshotKey, ClickAnalyticsResponse> snapshots = new ConcurrentHashMap<>();

    public ClickAnalyticsAgentService(VideoClickLogRepository videoClickLogRepository) {
        this.videoClickLogRepository = videoClickLogRepository;
    }

    @Transactional(readOnly = true)
    public ClickAnalyticsResponse getSnapshot(int periodDays, int limit) {
        SnapshotKey key = new SnapshotKey(safePeriod(periodDays), safeLimit(limit));
        return snapshots.computeIfAbsent(key, this::buildSnapshot);
    }

    @Scheduled(
            initialDelayString = "${analytics.clicks.initial-delay-ms:30000}",
            fixedDelayString = "${analytics.clicks.refresh-ms:1800000}"
    )
    @Transactional(readOnly = true)
    public void refreshSnapshots() {
        if (snapshots.isEmpty()) {
            SnapshotKey defaultKey = new SnapshotKey(DEFAULT_PERIOD_DAYS, DEFAULT_LIMIT);
            snapshots.put(defaultKey, buildSnapshot(defaultKey));
            return;
        }

        new ArrayList<>(snapshots.keySet()).forEach(key -> {
            try {
                snapshots.put(key, buildSnapshot(key));
            } catch (RuntimeException exception) {
                log.warn("[DOKHUB] 클릭 분석 스냅샷 갱신 실패(periodDays={}, limit={})",
                        key.periodDays(), key.limit(), exception);
            }
        });
    }

    private ClickAnalyticsResponse buildSnapshot(SnapshotKey key) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(key.periodDays() - 1L);
        LocalDateTime from = startDate.atStartOfDay();

        long totalClicks = videoClickLogRepository.countClicksSince(from);
        long uniqueVideos = videoClickLogRepository.countUniqueVideosSince(from);
        long uniqueChannels = videoClickLogRepository.countUniqueChannelsSince(from);

        List<ClickAnalyticsResponse.RankingItem> topVideos = toVideoRankings(
                videoClickLogRepository.findTopVideosSince(from, key.limit()), totalClicks);
        List<ClickAnalyticsResponse.RankingItem> topChannels = toChannelRankings(
                videoClickLogRepository.findTopChannelsSince(from, key.limit()), totalClicks);
        List<ClickAnalyticsResponse.DailyClick> dailyClicks = fillDailyClicks(
                startDate, today, videoClickLogRepository.findDailyClicksSince(from));

        String summary = buildSummary(key.periodDays(), totalClicks, uniqueVideos, uniqueChannels, topVideos, topChannels);

        return new ClickAnalyticsResponse(
                key.periodDays(),
                LocalDateTime.now().toString(),
                summary,
                totalClicks,
                uniqueVideos,
                uniqueChannels,
                topVideos,
                topChannels,
                dailyClicks
        );
    }

    private List<ClickAnalyticsResponse.RankingItem> toVideoRankings(List<Object[]> rows, long totalClicks) {
        return rows.stream().map(row -> new ClickAnalyticsResponse.RankingItem(
                text(row[0], "unknown"),
                text(row[1], "제목을 확인할 수 없는 영상"),
                text(row[2], "알 수 없는 채널"),
                number(row[3]),
                percent(number(row[3]), totalClicks)
        )).toList();
    }

    private List<ClickAnalyticsResponse.RankingItem> toChannelRankings(List<Object[]> rows, long totalClicks) {
        return rows.stream().map(row -> new ClickAnalyticsResponse.RankingItem(
                text(row[0], "unknown"),
                text(row[1], "알 수 없는 채널"),
                "채널 클릭",
                number(row[2]),
                percent(number(row[2]), totalClicks)
        )).toList();
    }

    private List<ClickAnalyticsResponse.DailyClick> fillDailyClicks(
            LocalDate startDate,
            LocalDate endDate,
            List<Object[]> rows
    ) {
        Map<LocalDate, Long> countsByDate = new LinkedHashMap<>();
        rows.forEach(row -> countsByDate.put(LocalDate.parse(row[0].toString()), number(row[1])));

        List<ClickAnalyticsResponse.DailyClick> result = new ArrayList<>();
        LocalDate cursor = startDate;
        while (!cursor.isAfter(endDate)) {
            result.add(new ClickAnalyticsResponse.DailyClick(
                    cursor.toString(),
                    countsByDate.getOrDefault(cursor, 0L)
            ));
            cursor = cursor.plusDays(1);
        }
        return result;
    }

    private String buildSummary(
            int periodDays,
            long totalClicks,
            long uniqueVideos,
            long uniqueChannels,
            List<ClickAnalyticsResponse.RankingItem> topVideos,
            List<ClickAnalyticsResponse.RankingItem> topChannels
    ) {
        if (totalClicks == 0) {
            return "최근 " + periodDays + "일 동안 집계된 영상 클릭이 없습니다.";
        }

        StringBuilder summary = new StringBuilder()
                .append("최근 ").append(periodDays).append("일 동안 ")
                .append(uniqueChannels).append("개 채널의 ")
                .append(uniqueVideos).append("개 영상에서 총 ")
                .append(totalClicks).append("회의 클릭이 집계되었습니다.");

        if (!topChannels.isEmpty()) {
            ClickAnalyticsResponse.RankingItem topChannel = topChannels.get(0);
            summary.append(" 가장 많이 선택된 채널은 ")
                    .append(topChannel.getLabel()).append("(")
                    .append(topChannel.getClickCount()).append("회)입니다.");
        }
        if (!topVideos.isEmpty()) {
            ClickAnalyticsResponse.RankingItem topVideo = topVideos.get(0);
            summary.append(" 가장 많이 본 영상은 ‘")
                    .append(topVideo.getLabel()).append("’(")
                    .append(topVideo.getClickCount()).append("회)입니다.");
        }
        return summary.toString();
    }

    private int safePeriod(int periodDays) {
        return Math.max(1, Math.min(periodDays, 90));
    }

    private int safeLimit(int limit) {
        return Math.max(3, Math.min(limit, 10));
    }

    private String text(Object value, String fallback) {
        if (value == null || value.toString().isBlank()) {
            return fallback;
        }
        return value.toString();
    }

    private long number(Object value) {
        return value instanceof Number number ? number.longValue() : 0L;
    }

    private double percent(long count, long total) {
        if (total <= 0) {
            return 0;
        }
        return Math.round((count * 1000.0) / total) / 10.0;
    }

    private record SnapshotKey(int periodDays, int limit) {
    }
}
