package com.example.auth.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig {

    @Value("${rate.limit.login.capacity:5}")
    private int capacity;

    @Value("${rate.limit.login.refill.tokens:5}")
    private int refillTokens;

    @Value("${rate.limit.login.refill.minutes:1}")
    private int refillMinutes;

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String key) {
        return cache.computeIfAbsent(key, k -> createNewBucket());
    }

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(
            capacity,
            Refill.intervally(refillTokens, Duration.ofMinutes(refillMinutes))
        );
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }
}
