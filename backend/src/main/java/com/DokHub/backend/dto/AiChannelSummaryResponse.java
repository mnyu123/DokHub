package com.DokHub.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiChannelSummaryResponse {

    private String category;
    private int periodDays;
    private String overallSummary;
    private Stats stats;
    private List<ChannelActivity> topActiveChannels;
    private List<ChannelActivity> recentlyInactiveChannels;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Stats {
        private int totalChannels;
        private int activeChannels;
        private int inactiveChannels;
        private int totalVideos;
        private String startDate;
        private String endDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChannelActivity {
        private String channelName;
        private int videoCount;
        private String lastUploadAt;
        private long daysSinceLastUpload;
    }
}