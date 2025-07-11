package com.cardcostapi.infrastructure;

import org.springframework.stereotype.Component;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

import java.util.Map;

@Component
public class InMemoryCache implements ICache{

    private final Cache<String, String> cache;

    public InMemoryCache() {
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
    }

    @Override
    public String get(String key) {
        return this.cache.getIfPresent(key);
    }

    @Override
    public void remove(String key) {
        this.cache.invalidate(key);
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
