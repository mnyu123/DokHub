package com.DokHub.backend.security;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
public class RequestRateLimiter {

    private static final int MAX_BUCKETS = 10_000;
    private final Map<String, WindowCounter> counters = new HashMap<>();

    public synchronized boolean allow(String key, int limit, Duration window) {
        if (limit <= 0) {
            return false;
        }
        long windowMillis = Math.max(1, window.toMillis());
        long now = System.currentTimeMillis();
        long currentWindow = now / windowMillis;
        WindowCounter existing = counters.get(key);
        if (existing == null) {
            if (counters.size() >= MAX_BUCKETS) {
                counters.entrySet().removeIf(entry -> entry.getValue().expiresAt <= now);
                if (counters.size() >= MAX_BUCKETS) {
                    return false;
                }
            }
            counters.put(key, new WindowCounter(currentWindow, (currentWindow + 1) * windowMillis, 1));
            return true;
        }
        if (existing.windowId != currentWindow) {
            counters.put(key, new WindowCounter(currentWindow, (currentWindow + 1) * windowMillis, 1));
            return true;
        }
        if (existing.count >= limit) {
            return false;
        }
        existing.count++;
        return true;
    }

    private static final class WindowCounter {
        private final long windowId;
        private final long expiresAt;
        private int count;

        private WindowCounter(long windowId, long expiresAt, int count) {
            this.windowId = windowId;
            this.expiresAt = expiresAt;
            this.count = count;
        }
    }
}
