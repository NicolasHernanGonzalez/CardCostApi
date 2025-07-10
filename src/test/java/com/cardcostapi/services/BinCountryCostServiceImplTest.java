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
    public void getClearingCostByBin_SuccessTest() {

        //SETUP
        String bin = "123456";
        String country = "AR";
        Long id = 1L;
        Double cost = 10.0;
        ClearingCost clearingCost = new ClearingCost(id,country, cost);

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
    public void getClearingCostByBin_CountryNotFoundTest() {
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
    public void getClearingCostByBin_CostNotFoundTest() {
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

    @Test
    public void getClearingCostByBin_UsingDefaultCountryTest() {
        //SETUP
        String bin = "123456";
        String country = "AR";
        String defaultCountry = "Others";
        ClearingCost clearingCost = new ClearingCost(1L,country, 77D);
        //MOCK
        when(binLookupService.getCountryByBin(bin)).thenReturn(country);
        when(clearingCostCrudService.getClearingCostByCountryId(country)).thenReturn(Optional.empty());
        when(clearingCostCrudService.getClearingCostByCountryId(defaultCountry)).thenReturn(Optional.of(clearingCost));

        //SUT
        BinCountryCost binCountryCost = binCountryCostService.getClearingCostByBin(bin);

        //ASSERTS
        assertEquals(defaultCountry,binCountryCost.getCountry());
        assertEquals(clearingCost.getClearingCost(),binCountryCost.getCost());
    }


}
