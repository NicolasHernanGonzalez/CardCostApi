package com.cardcostapi.infrastructure;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryLock implements ILock {

    private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

    @Override
    public Object aquire(String bin) {
        return locks.computeIfAbsent(bin, k -> new Object());
    }
}
