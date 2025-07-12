package com.cardcostapi.services;


import com.cardcostapi.exception.ExternalServiceErrorException;
import com.cardcostapi.external.BinDataResponse;
import com.cardcostapi.external.IBinLookupClient;
import com.cardcostapi.infrastructure.ICache;
import com.cardcostapi.infrastructure.InMemoryCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class BinLookupServiceImplTest {

    @Mock
    private IBinLookupClient binLookupClient;

    @Mock
    private IRateLimitService rateLimitService;
    private ICache cache;

    private BinLookupServiceImpl binLookupService;

    @BeforeEach
    void setUp() {
        cache = new InMemoryCache(60,1000);
        MockitoAnnotations.openMocks(this);
        binLookupService = new BinLookupServiceImpl(cache,binLookupClient, rateLimitService);
    }

    @Test
    public void getCountryByBin_SuccessTest() {

        //SETUP
        String bin = "12345";
        BinDataResponse.Country country = new BinDataResponse.Country("AR");
        BinDataResponse binDataResponse = new BinDataResponse(country);

        //MOCK
        when(rateLimitService.canProceed(anyString())).thenReturn(true);
        when(binLookupClient.getBinData(bin)).thenReturn(binDataResponse);
        doNothing().when(rateLimitService).registerKey(anyString());

        //SUT
        String countryByBin = binLookupService.getCountryByBin(bin);

        //ASSERT
        assertEquals("AR", countryByBin);
    }

    @Test
    public void getCountryByBin_RateLimitExceeded() {

        //SETUP
        String bin = "12345";

        //MOCK
        when(rateLimitService.canProceed(anyString())).thenReturn(false);

        //SUT
        assertThrows(ExecutionException.class, () -> {
            String countryByBin = binLookupService.getCountryByBin(bin);
        });
    }

    @Test
    public void getCountryByBin_ReturnNoDataNullCountry() {

        //SETUP
        String bin = "12345";
        BinDataResponse.Country country = new BinDataResponse.Country(null);
        BinDataResponse binDataResponse = new BinDataResponse(country);

        //MOCK
        when(rateLimitService.canProceed(anyString())).thenReturn(true);
        when(binLookupClient.getBinData(bin)).thenReturn(binDataResponse);
        doNothing().when(rateLimitService).registerKey(anyString());

        //SUT
        String countryByBin = binLookupService.getCountryByBin(bin);

        //ASSERT
        assertNull(countryByBin);
    }

    @Test
    public void getCountryByBin_ReturnNoDataNullResponse() {

        //SETUP
        String bin = "12345";

        //MOCK
        when(rateLimitService.canProceed(anyString())).thenReturn(true);
        when(binLookupClient.getBinData(bin)).thenReturn(null);
        doNothing().when(rateLimitService).registerKey(anyString());

        //SUT
        String countryByBin = binLookupService.getCountryByBin(bin);

        //ASSERT
        assertNull(countryByBin);
    }

    @Test
    public void testGetCountryByBin_CacheHit() {
        // SETUP
        String bin = "123456";
        Map<String, String> cache = new ConcurrentHashMap<>();
        cache.put(bin, "US");
        binLookupService.setCache(cache);

        // SUT
        String result = binLookupService.getCountryByBin(bin);

        // ASSERT
        assertEquals("US", result);
        verifyNoInteractions(binLookupClient, rateLimitService);
    }
    @Test
    public void testGetCountryByBin_CacheNotFound() {
        // SETUP
        String bin = "654321";
        this.cache.put(bin, "NOT_FOUND");
        binLookupService.setCache(cache.getCache());

        // MOCK
        when(rateLimitService.canProceed("binlist")).thenReturn(true);
        when(binLookupClient.getBinData(bin)).thenReturn(null);

        // SUT
        String result = binLookupService.getCountryByBin(bin);

        // ASSERT
        assertNull(result);
        verify(binLookupClient).getBinData(bin);
    }


    @Test
    public void testGetCountryByBin_CacheMiss_ValidResponse() {
        // SETUP
        String bin = "789012";
        BinDataResponse response = mock(BinDataResponse.class);

        // MOCK
        when(rateLimitService.canProceed("binlist")).thenReturn(true);
        when(response.isValid()).thenReturn(true);
        when(response.getCountryString()).thenReturn("AR");
        when(binLookupClient.getBinData(bin)).thenReturn(response);

        // SUT
        String result = binLookupService.getCountryByBin(bin);

        // ASSERT
        assertEquals("AR", result);
        assertEquals("AR", binLookupService.getCache().get(bin));
        verify(rateLimitService).registerKey("binlist");
    }

    @Test
    public void testGetCountryByBin_CacheMissNullResponse() {
        // SETUP
        String bin = "888888";

        // MOCK
        when(rateLimitService.canProceed("binlist")).thenReturn(true);
        when(binLookupClient.getBinData(bin)).thenReturn(null);

        // SUT
        String result = binLookupService.getCountryByBin(bin);

        // ASSERT
        assertNull(result);
        assertEquals("NOT_FOUND", binLookupService.getCache().get(bin));
        verify(rateLimitService).registerKey("binlist");
    }

    @Test
    public void testGetCountryByBin_RateLimitExceeded() {
        // SETUP
        String bin = "999999";

        // MOCK
        when(rateLimitService.canProceed("binlist")).thenReturn(false);

        // SUT + ASSERT
        assertThrows(ExecutionException.class, () -> binLookupService.getCountryByBin(bin));
        verifyNoInteractions(binLookupClient);
    }

    @Test
    public void testFallbackMethod() {
        // SUT + ASSERT
        ExternalServiceErrorException ex = assertThrows(
                ExternalServiceErrorException.class,
                () -> binLookupService.binApiFallback("123456", new RuntimeException("boom"))
        );
        assertTrue(ex.getMessage().contains("unstable"));
    }
}
