package com.mohsin.ratelimiter.config;

import com.mohsin.ratelimiter.model.RateLimiterType;

public class ClientConfig {
    private final String clientId;
    private final RateLimiterType rateLimiterType;
    private final int maxRequests;
    private final int refillRate; // Specific to Token Bucket and Leaky Bucket
    private final long windowSizeInSeconds; // Specific to Fixed Window and Sliding Window

    private ClientConfig(Builder builder) {
        this.clientId = builder.clientId;
        this.rateLimiterType = builder.rateLimiterType;
        this.maxRequests = builder.maxRequests;
        this.refillRate = builder.refillRate;
        this.windowSizeInSeconds = builder.windowSizeInSeconds;
    }

    public String getClientId() {
        return clientId;
    }

    public RateLimiterType getRateLimiterType() {
        return rateLimiterType;
    }

    public int getMaxRequests() {
        return maxRequests;
    }

    public int getRefillRate() {
        return refillRate;
    }

    public long getWindowSizeInSeconds() {
        return windowSizeInSeconds;
    }

    public static class Builder {
        private final String clientId;
        private final RateLimiterType rateLimiterType;
        private final int maxRequests;
        private int refillRate = 1; // default value
        private long windowSizeInSeconds = 60; // default value

        public Builder(String clientId, RateLimiterType rateLimiterType, int maxRequests) {
            this.clientId = clientId;
            this.rateLimiterType = rateLimiterType;
            this.maxRequests = maxRequests;
        }

        public Builder refillRate(int refillRate) {
            this.refillRate = refillRate;
            return this;
        }

        public Builder windowSizeInSeconds(long windowSizeInSeconds) {
            this.windowSizeInSeconds = windowSizeInSeconds;
            return this;
        }

        public ClientConfig build() {
            return new ClientConfig(this);
        }
    }
}
