package com.sportevents.sporteventsMVP.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitConfig {

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String ip, String path) {
        return buckets.computeIfAbsent(ip + ":" + path, key -> newBucket(path));
    }

    private Bucket newBucket(String path) {
        if (path.startsWith("/api/chat")) {
            // Chat con IA — costoso, limitá más
            return Bucket.builder()
                    .addLimit(Bandwidth.builder()
                            .capacity(5)
                            .refillGreedy(5, Duration.ofMinutes(1))
                            .build())
                    .build();
        }
        if (path.startsWith("/api/auth")) {
            // Auth — protección contra fuerza bruta
            return Bucket.builder()
                    .addLimit(Bandwidth.builder()
                            .capacity(10)
                            .refillGreedy(10, Duration.ofMinutes(5))
                            .build())
                    .build();
        }
        // Resto — navegación normal
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(60)
                        .refillGreedy(60, Duration.ofMinutes(1))
                        .build())
                .build();
    }
}