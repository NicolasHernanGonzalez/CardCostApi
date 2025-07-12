package com.cardcostapi.services;

import com.cardcostapi.config.InMemoryRateLimitConfig;
import com.cardcostapi.infrastructure.InMemoryRateLimit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryRateLimitTest {

    private InMemoryRateLimitConfig properties;
    private Map<String, Queue<Instant>> accessMap;
    private InMemoryRateLimit rateLimit;

    @BeforeEach
    void setUp() {
        accessMap = new ConcurrentHashMap<>();
        properties = new InMemoryRateLimitConfig();
    }

    @Test
    public void InMemoryRateLimit_Success() {
        //SETUP
        this.properties.setLimit(5);
        this.properties.setDuration(1);
        this.rateLimit = new InMemoryRateLimit(properties);

        //SUT
        boolean test = this.rateLimit.canProceed("test");

        assertTrue(test);
    }

    @Test
    public void InMemoryRateLimit_RateLimitExceeded() {
        //SETUP
        this.properties.setLimit(5);
        this.properties.setDuration(1);
        this.rateLimit = new InMemoryRateLimit(properties);
        this.rateLimit.setAccessMap(accessMap);

        //SUT & ASSERTS
        for (int cont = 0;cont< 5; cont++ ){
            assertTrue(rateLimit.canProceed("test"));
            rateLimit.registerKey("test");
        }

        boolean test = rateLimit.canProceed("test");
        assertFalse(test);
    }


    @Test
    public void InMemoryRateLimit_RemoveExpiredAccess() {
        // SETUP
        this.properties.setDuration(2); // duración en horas
        this.properties.setLimit(5);
        this.rateLimit = new InMemoryRateLimit(properties);
        this.accessMap = new ConcurrentHashMap<>();

        // Creamos la queue con accesos
        Queue<Instant> queue = new ConcurrentLinkedQueue<>();
        queue.add(Instant.now().minus(Duration.ofHours(6))); // expired
        queue.add(Instant.now().minus(Duration.ofHours(5))); // expired
        queue.add(Instant.now().minus(Duration.ofHours(1))); // not expired

        this.accessMap.put("test", queue);
        this.rateLimit.setAccessMap(accessMap); // importante hacer esto después

        // SUT
        this.rateLimit.canProceed("test");

        // ASSERT
        int size = accessMap.get("test").size();
        assertEquals(1, size);
    }
}