package com.DokHub.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClickAnalyticsResponse {

    private int periodDays;
    private String generatedAt;
    private String summary;
    private long totalClicks;
    private long uniqueVideos;
    private long uniqueChannels;
    private List<RankingItem> topVideos;
    private List<RankingItem> topChannels;
    private List<DailyClick> dailyClicks;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RankingItem {
        private String id;
        private String label;
        private String detail;
        private long clickCount;
        private double sharePercent;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyClick {
        private String date;
        private long clickCount;
    }
}
