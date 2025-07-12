package com.cardcostapi.services;

import com.cardcostapi.exception.ExternalServiceErrorException;
import com.cardcostapi.exception.TooManyRequestsException;
import com.cardcostapi.external.BinDataResponse;
import com.cardcostapi.external.IBinLookupClient;
import com.cardcostapi.infrastructure.ICache;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;


import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

@Primary
//@Profile("fake_client")
@Service
public class BinLookupServiceQueueImpl implements IBinLookupService{

    private final ICache cache;
    private final IBinLookupClient binLookupClient;
    private final IRateLimitService rateLimitService;

    private static final String NOT_FOUND = "NOT_FOUND";

    private final ConcurrentHashMap<String, CompletableFuture<String>> processingBins = new ConcurrentHashMap<>();

    public BinLookupServiceQueueImpl(ICache cache, IBinLookupClient client, IRateLimitService rateLimitService) {
        this.cache = cache;
        this.binLookupClient = client;
        this.rateLimitService = rateLimitService;
    }

    @SneakyThrows
    @Override
    @CircuitBreaker(name = "binlist", fallbackMethod = "binApiFallback")
    public String getCountryByBin(String bin) {

        String cached = getCachedValue(bin);
        if (cached != null) {
            return cached;
        }

        CompletableFuture<String> future = processingBins.compute(bin, (b, existingFuture) -> {
            if (existingFuture != null) {
                return existingFuture;
            }

            CompletableFuture<String> completableFuture = new CompletableFuture<>();

            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    if (!rateLimitService.canProceed("binlist")) {
                        completableFuture.completeExceptionally(new TooManyRequestsException("Rate limit exceeded for external service"));
                        return;
                    }

                    System.out.println("Calling external API for BIN: " + bin);

                    //EXTERNAL API CALL
                    BinDataResponse binData = binLookupClient.getBinData(bin);

                    rateLimitService.registerKey("binlist");

                    String result;
                    if (binData != null && binData.isValid()) {
                        result = binData.getCountryString();
                        System.out.println("Putting value: " + result + " in cache for key: " + bin);
                    } else {
                        result = NOT_FOUND;
                        System.out.println("Putting NULL value in cache for key: " + bin);
                    }

                    cache.put(bin, result);
                    completableFuture.complete(NOT_FOUND.equals(result) ? null : result);

                } catch (Exception e) {
                    completableFuture.completeExceptionally(e);
                } finally {
                    processingBins.remove(bin);
                }
            });

            return completableFuture;
        });

        return future.get(); // Espera sincrónica
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

        Throwable cause = (ex instanceof ExecutionException) ? ex.getCause() : ex;

        if (cause instanceof TooManyRequestsException) {
            throw (TooManyRequestsException) cause;
        }

        throw new ExternalServiceErrorException("BinData system is unstable, please try again later.Bin: " + bin + ". " + ex.getMessage());
    }

    //for test purposes
    public void setCache(Map<String, String> cache) {
        this.cache.clear();
        this.cache.putAll(cache);
    }

    public Map<String, String> getCache() {
        return this.cache.getCache();
    }

}
