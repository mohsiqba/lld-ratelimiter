package com.mohsin.ratelimiter.factory;

import com.mohsin.ratelimiter.config.ClientConfig;
import com.mohsin.ratelimiter.strategy.*;

public class RateLimiterFactory {
    private static volatile RateLimiterFactory INSTANCE;

    private RateLimiterFactory() {
    }

    public static RateLimiterFactory getInstance() {
        if (INSTANCE == null) {
            synchronized (RateLimiterFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RateLimiterFactory();
                }
            }
        }
        return INSTANCE;
    }

    public RateLimiter createRateLimiter(ClientConfig config) {
        switch (config.getRateLimiterType()) {
            case TOKEN_BUCKET:
                TokenBucketRateLimiter tokenBucket = new TokenBucketRateLimiter(
                        config.getMaxRequests(),
                        config.getRefillRate()
                );
                tokenBucket.start();
                return tokenBucket;
            case LEAKY_BUCKET:
                LeakyBucketRateLimiter leakyBucket = new LeakyBucketRateLimiter(
                        config.getMaxRequests(),
                        config.getRefillRate()
                );
                leakyBucket.start();
                return leakyBucket;
            case FIXED_WINDOW:
                FixedWindowRateLimiter fixedWindow = new FixedWindowRateLimiter(
                        config.getMaxRequests(),
                        config.getWindowSizeInSeconds()
                );
                fixedWindow.start();
                return fixedWindow;
            case SLIDING_WINDOW:
                SlidingWindowRateLimiter slidingWindow = new SlidingWindowRateLimiter(
                        config.getMaxRequests(),
                        config.getWindowSizeInSeconds()
                );
                slidingWindow.start();
                return slidingWindow;
            // Add cases for other rate limiter types
            default:
                throw new UnsupportedOperationException("Rate limiter type not supported.");
        }
    }
}
