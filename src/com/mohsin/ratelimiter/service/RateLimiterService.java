package com.mohsin.ratelimiter.service;

import com.mohsin.ratelimiter.config.ClientConfig;
import com.mohsin.ratelimiter.factory.RateLimiterFactory;
import com.mohsin.ratelimiter.strategy.RateLimiter;

import java.util.concurrent.ConcurrentHashMap;

public class RateLimiterService {
    private final ConcurrentHashMap<String, RateLimiter> clientRateLimiters;
    private final ConcurrentHashMap<String, ClientConfig> clientConfigs;

    public RateLimiterService() {
        this.clientRateLimiters = new ConcurrentHashMap<>();
        this.clientConfigs = new ConcurrentHashMap<>();
    }

    /**
     * Registers a client with a specific rate limiting configuration.
     *
     * @param config The client's rate limiting configuration.
     */
    public void registerClient(ClientConfig config) {
        clientConfigs.put(config.getClientId(), config);
        RateLimiter rateLimiter = RateLimiterFactory.getInstance().createRateLimiter(config);
        clientRateLimiters.put(config.getClientId(), rateLimiter);
    }

    /**
     * Validates whether a request from the specified client should be allowed.
     *
     * @param clientId The ID of the client making the request.
     * @return true if the request is allowed, false otherwise.
     */
    public boolean validateRequest(String clientId) {
        RateLimiter rateLimiter = clientRateLimiters.get(clientId);
        if (rateLimiter == null) {
            throw new IllegalArgumentException("Client not registered.");
        }
        return rateLimiter.allowRequest();
    }

    /**
     * Unregisters a client and stops its rate limiter.
     *
     * @param clientId The ID of the client to unregister.
     */
    public void unregisterClient(String clientId) {
        RateLimiter rateLimiter = clientRateLimiters.remove(clientId);
        if (rateLimiter != null) {
            rateLimiter.stop();
        }
        clientConfigs.remove(clientId);
    }
}
