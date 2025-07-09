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

    private Map<String, Queue<Instant>> accessMap;
    private int limit;
    private Duration window;


    public InMemoryRateLimit(InMemoryRateLimitConfig properties) {
        this.limit = properties.getLimit();
        this.window = Duration.ofHours(properties.getDuration());
        this.accessMap = new ConcurrentHashMap<>();
    }

    @Override
    public boolean canProceed(String key) {

        Queue<Instant> timestamps = accessMap.get(key);

        if (timestamps == null) {
            timestamps = new ConcurrentLinkedQueue<>();
            accessMap.put(key, timestamps);
        }

        //Hoy menos la ventana de tiempo
        Instant cutoff = Instant.now().minus(window);

        //Limpia la ventana de tiempo con los vencidos
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
    }

    //only for testing purposes
    public Map<String, Queue<Instant>> getAccessMap() {
        return accessMap;
    }

    public void setAccessMap(Map<String, Queue<Instant>> accessMap) {
        this.accessMap = accessMap;
    }

}