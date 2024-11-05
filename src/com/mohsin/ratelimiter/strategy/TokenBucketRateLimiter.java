package com.mohsin.ratelimiter.strategy;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TokenBucketRateLimiter implements RateLimiter {
    private final int maxTokens;
    private final int refillRate; // tokens per second
    private final AtomicInteger tokens;
    private final ScheduledExecutorService scheduler;

    public TokenBucketRateLimiter(int maxTokens, int refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.tokens = new AtomicInteger(maxTokens);
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public boolean allowRequest() {
        while (true) {
            int currentTokens = tokens.get();
            if (currentTokens <= 0) {
                return false;
            }
            if (tokens.compareAndSet(currentTokens, currentTokens - 1)) {
                return true;
            }
        }
    }

    @Override
    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            int currentTokens = tokens.get();
            if (currentTokens < maxTokens) {
                tokens.incrementAndGet();
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        scheduler.shutdown();
    }
}
