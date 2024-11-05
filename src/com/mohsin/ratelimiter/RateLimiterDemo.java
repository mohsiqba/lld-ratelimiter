package com.mohsin.ratelimiter;

import com.mohsin.ratelimiter.config.ClientConfig;
import com.mohsin.ratelimiter.model.RateLimiterType;
import com.mohsin.ratelimiter.service.RateLimiterService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RateLimiterDemo {
    public static void main(String[] args) throws InterruptedException {
        RateLimiterService rateLimiterService = new RateLimiterService();

        // Register clients with specific configurations
        ClientConfig tokenBucketConfig = new ClientConfig.Builder("clientA", RateLimiterType.TOKEN_BUCKET, 5)
                .refillRate(2) // 2 tokens per second
                .build();

        ClientConfig leakyBucketConfig = new ClientConfig.Builder("clientB", RateLimiterType.LEAKY_BUCKET, 10)
                .refillRate(5) // leaks 5 requests per second
                .build();

        ClientConfig fixedWindowConfig = new ClientConfig.Builder("clientC", RateLimiterType.FIXED_WINDOW, 100)
                .windowSizeInSeconds(60) // 1 minute window
                .build();

        ClientConfig slidingWindowConfig = new ClientConfig.Builder("clientD", RateLimiterType.SLIDING_WINDOW, 50)
                .windowSizeInSeconds(60) // 1 minute sliding window
                .build();

        rateLimiterService.registerClient(tokenBucketConfig);
        rateLimiterService.registerClient(leakyBucketConfig);
        rateLimiterService.registerClient(fixedWindowConfig);
        rateLimiterService.registerClient(slidingWindowConfig);

        // Simulate concurrent requests
        ExecutorService executor = Executors.newFixedThreadPool(20);

        Runnable clientATask = () -> {
            String clientId = "clientA";
            if (rateLimiterService.validateRequest(clientId)) {
                System.out.println("Client A (Token Bucket): Request allowed.");
            } else {
                System.out.println("Client A (Token Bucket): Rate limit exceeded.");
            }
        };

        Runnable clientBTask = () -> {
            String clientId = "clientB";
            if (rateLimiterService.validateRequest(clientId)) {
                System.out.println("Client B (Leaky Bucket): Request allowed.");
            } else {
                System.out.println("Client B (Leaky Bucket): Rate limit exceeded.");
            }
        };

        Runnable clientCTask = () -> {
            String clientId = "clientC";
            if (rateLimiterService.validateRequest(clientId)) {
                System.out.println("Client C (Fixed Window): Request allowed.");
            } else {
                System.out.println("Client C (Fixed Window): Rate limit exceeded.");
            }
        };

        Runnable clientDTask = () -> {
            String clientId = "clientD";
            if (rateLimiterService.validateRequest(clientId)) {
                System.out.println("Client D (Sliding Window): Request allowed.");
            } else {
                System.out.println("Client D (Sliding Window): Rate limit exceeded.");
            }
        };

        // Submit tasks
        for (int i = 0; i < 50; i++) {
            executor.submit(clientATask);
            executor.submit(clientBTask);
            executor.submit(clientCTask);
            executor.submit(clientDTask);
        }

        // Shutdown executor
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        // Unregister clients
        rateLimiterService.unregisterClient("clientA");
        rateLimiterService.unregisterClient("clientB");
        rateLimiterService.unregisterClient("clientC");
        rateLimiterService.unregisterClient("clientD");
    }
}
