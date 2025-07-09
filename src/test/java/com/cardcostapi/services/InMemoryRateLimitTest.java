package com.cardcostapi.services;

import com.cardcostapi.config.InMemoryRateLimitConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
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
        this.properties.setLimit(5);
        this.properties.setDuration(1);
        this.rateLimit = new InMemoryRateLimit(properties);
        boolean test = this.rateLimit.canProceed("test");
        assertTrue(test);
    }

    @Test
    public void InMemoryRateLimit_RateLimitExceeded() {

        this.properties.setLimit(5);
        this.properties.setDuration(1);
        this.rateLimit = new InMemoryRateLimit(properties);
        this.rateLimit.setAccessMap(accessMap);

        for (int cont = 0;cont< 5; cont++ ){
            assertTrue(rateLimit.canProceed("test"));
            rateLimit.registerKey("test");
        }

        boolean test = rateLimit.canProceed("test");
        assertFalse(test);
    }


    @Test
    public void InMemoryRateLimit_RemoveExpiredAccess() {

        this.properties.setDuration(2);
        this.properties.setLimit(5);
        this.rateLimit = new InMemoryRateLimit(properties);
        this.rateLimit.setAccessMap(accessMap);
        this.rateLimit.canProceed("test");

        Queue<Instant> test = this.accessMap.get("test");

        //Not expired access
        test.add(Instant.now().minus(Duration.ofHours(6)));

        //Expired Access to remove
        test.add(Instant.now().minus(Duration.ofHours(5)));
        test.add(Instant.now().minus(Duration.ofHours(1)));

        this.rateLimit.canProceed("test");

        //Only one not expired access
        int size = accessMap.get("test").size();
        assertEquals(1,size);
    }
}