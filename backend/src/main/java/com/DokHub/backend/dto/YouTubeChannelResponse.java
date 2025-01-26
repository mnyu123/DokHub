package com.DokHub.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class YouTubeChannelResponse {
    private List<Item> items;

    @Data
    public static class Item {
        private String id;
        private Snippet snippet;

        @Data
        public static class Snippet {
            private Thumbnails thumbnails;

            @Data
            public static class Thumbnails {
                @JsonProperty("default")
                private Thumbnail defaultThumbnail;

                @Data
                public static class Thumbnail {
                    private String url;
                }
            }
        }
    }
}

