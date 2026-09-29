package com.DokHub.backend.service;

import com.DokHub.backend.dto.YouTubeSearchResponse;
import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class YouTubeServiceTest {

    @Test
    void overlappingChannelPagesOnlyFetchMissingThumbnails() {
        RestTemplate template = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(template).build();
        YouTubeService service = new YouTubeService(template, "test-key");
        server.expect(requestTo("https://www.googleapis.com/youtube/v3/channels?part=snippet&id=one&key=test-key"))
                .andRespond(withSuccess("{\"items\":[{\"id\":\"one\",\"snippet\":{\"thumbnails\":{\"default\":{\"url\":\"first\"}}}}]}", MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://www.googleapis.com/youtube/v3/channels?part=snippet&id=two&key=test-key"))
                .andRespond(withSuccess("{\"items\":[{\"id\":\"two\",\"snippet\":{\"thumbnails\":{\"default\":{\"url\":\"second\"}}}}]}", MediaType.APPLICATION_JSON));

        service.getChannelThumbnailsBatch(List.of("one"));
        assertEquals(2, service.getChannelThumbnailsBatch(List.of("one", "two")).size());
        assertEquals("first", service.getChannelThumbnailCached("one"));
        server.verify();
    }

    @Test
    void concurrentVideoRequestsShareOneExternalCall() throws Exception {
        RestTemplate template = mock(RestTemplate.class);
        YouTubeSearchResponse response = new YouTubeSearchResponse();
        response.setItems(List.of());
        CountDownLatch requested = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        when(template.getForObject(anyString(), eq(YouTubeSearchResponse.class))).thenAnswer(invocation -> {
            requested.countDown();
            assertTrue(release.await(5, TimeUnit.SECONDS));
            return response;
        });
        YouTubeService service = new YouTubeService(template, "test-key");
        ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            List<CompletableFuture<Void>> requests = new ArrayList<>();
            for (int index = 0; index < 8; index++) {
                requests.add(CompletableFuture.runAsync(() -> service.getChannelVideosCached("channel", 25), executor));
            }
            assertTrue(requested.await(5, TimeUnit.SECONDS));
            release.countDown();
            CompletableFuture.allOf(requests.toArray(new CompletableFuture<?>[0])).get(5, TimeUnit.SECONDS);
            verify(template, times(1)).getForObject(anyString(), eq(YouTubeSearchResponse.class));
        } finally {
            release.countDown();
            executor.shutdownNow();
        }
    }

    @Test
    void distinctVideoRequestsCannotGrowCacheBeyondItsLimit() {
        RestTemplate template = mock(RestTemplate.class);
        YouTubeSearchResponse response = new YouTubeSearchResponse();
        response.setItems(List.of());
        when(template.getForObject(anyString(), eq(YouTubeSearchResponse.class))).thenReturn(response);
        YouTubeService service = new YouTubeService(template, "test-key");
        for (int index = 0; index < 500; index++) {
            service.getChannelVideosCached("channel-" + index, 25);
        }
        Cache<?, ?> cache = (Cache<?, ?>) ReflectionTestUtils.getField(service, "channelVideosCache");
        assertNotNull(cache);
        cache.cleanUp();
        assertTrue(cache.estimatedSize() <= 128);
        service.refreshCache();
        assertEquals(0, cache.estimatedSize());
    }
}
