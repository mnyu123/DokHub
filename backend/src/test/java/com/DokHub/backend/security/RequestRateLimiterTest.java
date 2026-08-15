package com.DokHub.backend.security;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestRateLimiterTest {

    @Test
    void blocksRequestsOverTheWindowLimit() {
        RequestRateLimiter limiter = new RequestRateLimiter();

        assertTrue(limiter.allow("client", 2, Duration.ofMinutes(1)));
        assertTrue(limiter.allow("client", 2, Duration.ofMinutes(1)));
        assertFalse(limiter.allow("client", 2, Duration.ofMinutes(1)));
    }
}
