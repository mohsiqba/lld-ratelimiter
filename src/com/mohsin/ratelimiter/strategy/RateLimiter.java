package com.mohsin.ratelimiter.strategy;

public interface RateLimiter {
    /**
     * Determines whether a request is allowed.
     *
     * @return true if the request is allowed, false otherwise.
     */
    boolean allowRequest();

    /**
     * Initializes any scheduled tasks required by the rate limiter.
     */
    void start();

    /**
     * Shuts down any scheduled tasks gracefully.
     */
    void stop();
}
