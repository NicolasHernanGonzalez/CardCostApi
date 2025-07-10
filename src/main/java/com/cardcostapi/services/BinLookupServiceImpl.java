package com.cardcostapi.services;

import com.cardcostapi.exception.ExternalServiceErrorException;
import com.cardcostapi.exception.TooManyRequestsException;
import com.cardcostapi.external.BinDataResponse;
import com.cardcostapi.external.IBinLookupClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class BinLookupServiceImpl implements IBinLookupService {

    private final IBinLookupClient binLookupClient;

    private final IRateLimitService rateLimitService;

    public BinLookupServiceImpl(IBinLookupClient binLookupClient, IRateLimitService rateLimitService) {
        this.binLookupClient = binLookupClient;
        this.rateLimitService = rateLimitService;
    }

    @Cacheable("binCountryCache")
    @CircuitBreaker(name = "binlist", fallbackMethod = "binApiFallback")
    @Override
    public String getCountryByBin(String bin) {

        System.out.println("Calling Binlist for BIN: " + bin);

        if (!rateLimitService.canProceed("binlist")) {
            throw new TooManyRequestsException("Rate limit for binlist exceeded");
        }

        BinDataResponse binData = this.binLookupClient.getBinData(bin);

        rateLimitService.registerKey("binlist");

        if (binData != null && binData.getCountry() != null && binData.getCountry().getAlpha2() != null) {
            return binData.getCountry().getAlpha2();
        }

        return null;
    }

    public String binApiFallback(String bin){
        throw new ExternalServiceErrorException("BinData system is unstable, please try again later");
    }

}