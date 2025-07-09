package com.cardcostapi.services;


import com.cardcostapi.exception.TooManyRequestsException;
import com.cardcostapi.external.BinDataResponse;
import com.cardcostapi.external.IBinLookupClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class BinLookupServiceImplTest {

    @Mock
    private IBinLookupClient binLookupClient;

    @Mock
    private IRateLimitService rateLimitService;

    //@InjectMocks
    private BinLookupServiceImpl binLookupService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        binLookupService = new BinLookupServiceImpl(binLookupClient, rateLimitService);
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
        assertThrows(TooManyRequestsException.class, () -> {
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

        String countryByBin = binLookupService.getCountryByBin(bin);

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

        String countryByBin = binLookupService.getCountryByBin(bin);

        assertNull(countryByBin);
    }

}
