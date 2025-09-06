package com.DokHub.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class YouTubePlaylistItemsResponse {
    private List<Item> items;

    @Data
    public static class Item {
        private Snippet snippet;

        @Data
        public static class Snippet {
            private String title;
            private String publishedAt;
            private ResourceId resourceId;
            private Thumbnails thumbnails;

            @Data
            public static class ResourceId {
                private String videoId;
            }

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