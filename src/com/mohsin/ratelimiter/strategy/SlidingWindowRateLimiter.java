package com.mohsin.ratelimiter.strategy;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * maxRequests: Maximum number of allowed requests within the sliding window.
 * windowSizeInMillis: Duration of the sliding window in milliseconds.
 * ConcurrentLinkedQueue timestamps: Stores the timestamps of incoming requests.
 * allowRequest(): Removes outdated timestamps and checks if a new request can be allowed.
 */
public class SlidingWindowRateLimiter implements RateLimiter {
    private final int maxRequests;
    private final long windowSizeInMillis;
    private final ConcurrentLinkedQueue<Long> timestamps;

    public SlidingWindowRateLimiter(int maxRequests, long windowSizeInSeconds) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInSeconds * 1000;
        this.timestamps = new ConcurrentLinkedQueue<>();
    }

    @Override
    public boolean allowRequest() {
        long now = System.currentTimeMillis();
        long windowStart = now - windowSizeInMillis;

        // Remove timestamps outside the sliding window
        while (!timestamps.isEmpty() && timestamps.peek() < windowStart) {
            timestamps.poll();
        }

        if (timestamps.size() < maxRequests) {
            timestamps.add(now);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void start() {
        // No scheduled tasks needed for Sliding Window
    }

    @Override
    public void stop() {
        // No scheduled tasks to stop
    }
}
