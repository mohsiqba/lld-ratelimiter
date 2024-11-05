package com.mohsin.ratelimiter.strategy;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Capacity: Maximum number of requests the bucket can hold.
 * Leak Rate: Number of requests to process (leak) per second.
 * AtomicInteger currentSize: Keeps track of the current number of requests in the bucket.
 * ScheduledExecutorService: Periodically leaks requests at the specified leak rate.
 */
public class LeakyBucketRateLimiter implements RateLimiter {
    private final int capacity;
    private final int leakRate; // leaks per second
    private final AtomicInteger currentSize;
    private final ScheduledExecutorService scheduler;

    public LeakyBucketRateLimiter(int capacity, int leakRate) {
        this.capacity = capacity;
        this.leakRate = leakRate;
        this.currentSize = new AtomicInteger(0);
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public boolean allowRequest() {
        while (true) {
            int size = currentSize.get();
            if (size >= capacity) {
                return false; // Bucket is full
            }
            if (currentSize.compareAndSet(size, size + 1)) {
                return true; // Request added to the bucket
            }
        }
    }

    @Override
    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            int size = currentSize.get();
            if (size > 0) {
                currentSize.decrementAndGet(); // Leak one request
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        scheduler.shutdown();
    }
}
