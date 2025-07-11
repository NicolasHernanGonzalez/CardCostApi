package com.cardcostapi.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

import java.util.Map;

@Component
public class InMemoryCache implements ICache{

    private final Cache<String, String> cache;

    public InMemoryCache(@Value("${cache.ttl.minutes}") long ttlMinutes, @Value("${cache.max.size}") long size) {
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(ttlMinutes, TimeUnit.MINUTES)
                .maximumSize(size)
                .build();
    }

    @Override
    public String get(String key) {
        return this.cache.getIfPresent(key);
    }

    @Override
    public void clear() {
        this.cache.invalidateAll();
    }

    @Override
    public void put(String key, String value) {
        this.cache.put(key, value);
    }

    @Override
    public void putAll(Map<String, String> cache) {
        this.cache.putAll(cache);
    }

    @Override
    public Map<String, String> getCache() {
        return cache.asMap();
    }
}
