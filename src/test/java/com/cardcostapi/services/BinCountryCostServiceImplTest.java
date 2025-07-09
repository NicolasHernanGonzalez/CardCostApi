package com.cardcostapi.services;


import com.cardcostapi.controller.response.BinCountryCost;
import com.cardcostapi.domain.ClearingCost;
import com.cardcostapi.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import static org.mockito.Mockito.when;

public class BinCountryCostServiceImplTest {

    @Mock
    private IBinLookupService binLookupService;

    @Mock
    private IClearingCostCrudService clearingCostCrudService;

    @InjectMocks
    private BinCountryCostServiceImpl binCountryCostService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getClearingCostByBinSuccessTest() {

        //SETUP
        String bin = "123456";
        String country = "AR";
        Double cost = 10.0;
        ClearingCost clearingCost = new ClearingCost(country, cost);

        //MOCK
        when(binLookupService.getCountryByBin(bin)).thenReturn(country);
        when(clearingCostCrudService.getClearingCostByCountryId(country)).thenReturn(Optional.of(clearingCost));

        //SUT
        BinCountryCost clearingCost1 = binCountryCostService.getClearingCostByBin(bin);

        //ASSERTS
        assertEquals(cost, clearingCost1.getCost());
        assertEquals(country, clearingCost1.getCountry());
    }

    @Test
    public void getClearingCostByBinCountryNotFoundTest() {
        //SETUP
        String bin = "123456";

        //MOCK
        when(binLookupService.getCountryByBin(bin)).thenReturn(null);

        //SUT
        assertThrows(BusinessException.class, () -> {
            binCountryCostService.getClearingCostByBin(bin);
        });
    }

    @Test
    public void getClearingCostByBinCostNotFoundTest() {
        //SETUP
        String bin = "123456";
        String country = "AR";

        //MOCK
        when(binLookupService.getCountryByBin(bin)).thenReturn(country);
        when(clearingCostCrudService.getClearingCostByCountryId(country)).thenReturn(Optional.empty());

        //SUT
        assertThrows(BusinessException.class, () -> {
            binCountryCostService.getClearingCostByBin(bin);
        });
    }

}
