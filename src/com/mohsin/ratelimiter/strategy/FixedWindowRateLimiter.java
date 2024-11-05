package com.mohsin.ratelimiter.strategy;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * maxRequests: Maximum number of allowed requests per window.
 * windowSizeInSeconds: Duration of each fixed window.
 * AtomicInteger requestCount: Counts the number of requests in the current window.
 * ScheduledExecutorService: Resets the counter at the start of each new window.
 */
public class FixedWindowRateLimiter implements RateLimiter {
    private final int maxRequests;
    private final long windowSizeInSeconds;
    private final AtomicInteger requestCount;
    private final ScheduledExecutorService scheduler;

    public FixedWindowRateLimiter(int maxRequests, long windowSizeInSeconds) {
        this.maxRequests = maxRequests;
        this.windowSizeInSeconds = windowSizeInSeconds;
        this.requestCount = new AtomicInteger(0);
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public boolean allowRequest() {
        int currentCount = requestCount.incrementAndGet();
        if (currentCount > maxRequests) {
            return false;
        }
        return true;
    }

    @Override
    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            requestCount.set(0); // Reset the counter at the start of each window
        }, windowSizeInSeconds, windowSizeInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        scheduler.shutdown();
    }
}
