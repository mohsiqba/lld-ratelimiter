package com.mohsin.ratelimiter.model;

public enum RateLimiterType {
    TOKEN_BUCKET,
    LEAKY_BUCKET,
    FIXED_WINDOW,
    SLIDING_WINDOW
    // Future algorithms can be added here
}
