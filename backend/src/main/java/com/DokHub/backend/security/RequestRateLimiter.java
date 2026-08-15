package com.DokHub.backend.security;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class RequestRateLimiter {

    private static final int MAX_BUCKETS = 10_000;
    private final ConcurrentHashMap<String, WindowCounter> counters = new ConcurrentHashMap<>();

    public boolean allow(String key, int limit, Duration window) {
        long windowMillis = Math.max(1, window.toMillis());
        long currentWindow = System.currentTimeMillis() / windowMillis;
        AtomicBoolean allowed = new AtomicBoolean(false);

        counters.compute(key, (ignored, existing) -> {
            if (existing == null || existing.windowId != currentWindow) {
                allowed.set(true);
                return new WindowCounter(currentWindow, 1);
            }
            if (existing.count < limit) {
                existing.count++;
                allowed.set(true);
            }
            return existing;
        });

        if (counters.size() > MAX_BUCKETS) {
            counters.entrySet().removeIf(entry -> entry.getValue().windowId < currentWindow - 1);
        }
        return allowed.get();
    }

    private static final class WindowCounter {
        private final long windowId;
        private int count;

        private WindowCounter(long windowId, int count) {
            this.windowId = windowId;
            this.count = count;
        }
    }
}
