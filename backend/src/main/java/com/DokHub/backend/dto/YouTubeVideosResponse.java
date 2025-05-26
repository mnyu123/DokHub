package com.DokHub.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class YouTubeVideosResponse {
    private List<Item> items;

    @Data
    public static class Item {
        private String id;
        private Status status;
        private Snippet snippet;   // 썸네일을 더 선명한 medium/hq로 쓰고 싶을 때 사용

        @Data
        public static class Status {
            private String uploadStatus;   // processed, uploaded, failed ...
            private String privacyStatus;  // public, private, unlisted
            private Boolean embeddable;    // true / false
        }

        @Data
        public static class Snippet {
            private Thumbnails thumbnails;

            @Data
            public static class Thumbnails {
                private Thumbnail medium;
                private Thumbnail high;

                @Data
                public static class Thumbnail {
                    private String url;
                }
            }
        }
    }
}
