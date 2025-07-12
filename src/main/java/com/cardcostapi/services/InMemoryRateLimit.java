package com.cardcostapi.services;

import com.cardcostapi.config.InMemoryRateLimitConfig;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class InMemoryRateLimit implements IRateLimitService {

    private final Map<String, Queue<Instant>> accessMap;
    private final int limit;
    private final Duration window;

    public InMemoryRateLimit(InMemoryRateLimitConfig properties) {
        this.limit = properties.getLimit();                         // e.g. 5
        this.window = Duration.ofHours(properties.getDuration());   // e.g. 1
        this.accessMap = new ConcurrentHashMap<>();
    }

    @Override
    public boolean canProceed(String serviceName) {
        Queue<Instant> timestamps = accessMap.get(serviceName);

        if (timestamps == null) {
            timestamps = new ConcurrentLinkedQueue<>();
            accessMap.put(serviceName, timestamps);
        }

        Instant cutoff = Instant.now().minus(window);

        while (!timestamps.isEmpty() && timestamps.peek().isBefore(cutoff)) {
            timestamps.poll();
        }

        return timestamps.size() < limit;
    }

    @Override
    public void registerKey(String key) {

        Queue<Instant> timestamps = accessMap.get(key);
        if (timestamps == null) {
            timestamps = new ConcurrentLinkedQueue<>();
            accessMap.put(key, timestamps);
        }
        timestamps.offer(Instant.now());
        System.out.println("Aumentando RL: " + key);
    }

    // Solo para testing
    public Map<String, Queue<Instant>> getAccessMap() {
        return accessMap;
    }

    public void setAccessMap(Map<String, Queue<Instant>> accessMap) {
        this.accessMap.clear();
        this.accessMap.putAll(accessMap);
    }
}
