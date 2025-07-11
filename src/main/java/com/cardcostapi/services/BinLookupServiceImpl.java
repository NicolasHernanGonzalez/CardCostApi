package com.cardcostapi.services;

import com.cardcostapi.exception.ExternalServiceErrorException;
import com.cardcostapi.exception.TooManyRequestsException;
import com.cardcostapi.external.BinDataResponse;
import com.cardcostapi.external.IBinLookupClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BinLookupServiceImpl implements IBinLookupService {

    private final IBinLookupClient binLookupClient;
    private final IRateLimitService rateLimitService;
    private static final String NOT_FOUND = "NOT_FOUND";

    // Cache simple en memoria
    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

    // Locks por BIN para evitar múltiples llamadas concurrentes al mismo tiempo
    private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

    public BinLookupServiceImpl(@Qualifier("RealBinClient")IBinLookupClient binLookupClient, IRateLimitService rateLimitService) {
        this.binLookupClient = binLookupClient;
        this.rateLimitService = rateLimitService;
    }

    @Override
    @CircuitBreaker(name = "binlist", fallbackMethod = "binApiFallback")
    public String getCountryByBin(String bin) {
        String cached = getCachedValue(bin);
        if (cached != null) return cached;

        Object lock = locks.computeIfAbsent(bin, k -> new Object());
        synchronized (lock) {
            cached = getCachedValue(bin);
            if (cached != null) return cached;

            return fetchAndCache(bin);
        }
    }

    private String fetchAndCache(String bin) {
        //Check RL
        if (!rateLimitService.canProceed("binlist")) {
            throw new TooManyRequestsException("Rate limit for binlist exceeded");
        }
        //PROTECTED RESOURCE
        BinDataResponse binData = binLookupClient.getBinData(bin);

        //Update RL
        rateLimitService.registerKey("binlist");

        if (binData != null && binData.isValid()) {
            String theCountry = binData.getCountryString();
            System.out.println("Putting value: " + theCountry + " in cache for key: " + bin);
            cache.put(bin, theCountry);
            return theCountry;
        }

        System.out.println("Putting NULL value in cache for key: " + bin);
        cache.put(bin, NOT_FOUND);
        return null;
    }

    private String getCachedValue(String bin) {
        String cached = cache.get(bin);
        if (cached != null) {
            if (NOT_FOUND.equals(cached)) {
                System.out.println("Returning cached value NULL for key " + bin);
                return null;
            } else {
                System.out.println("Returning cached value: " + cached + " for key: " + bin);
                return cached;
            }
        }
        return null;
    }

    public String binApiFallback(String bin, Throwable ex){
        throw new ExternalServiceErrorException("BinData system is unstable, please try again later.Bin: " + bin + ". " + ex.getMessage());
    }

    //for test purposes
    public void setCache(Map<String, String> cache) {
        this.cache.clear();
        this.cache.putAll(cache);
    }

    public Map<String, String> getCache() {
        return this.cache;
    }
}
