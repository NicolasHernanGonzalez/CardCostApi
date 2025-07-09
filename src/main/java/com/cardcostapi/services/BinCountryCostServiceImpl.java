package com.cardcostapi.services;

import com.cardcostapi.controller.response.BinCountryCost;
import com.cardcostapi.domain.ClearingCost;
import com.cardcostapi.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BinCountryCostServiceImpl implements IBinCountryCostService{

    private static final String DEFAULT_COUNTRY = "Others";
    private final IBinLookupService binLookupService;
    private final IClearingCostCrudService clearingCostCrudService;

    public BinCountryCostServiceImpl(IBinLookupService binLookupService,IClearingCostCrudService clearingCostCrudService) {
        this.binLookupService = binLookupService;
        this.clearingCostCrudService = clearingCostCrudService;
    }

    @Override
    public BinCountryCost getClearingCostByBin(String bin) {

        //External Api call here!
        String country = this.binLookupService.getCountryByBin(bin);

        if (country == null) {
            throw new BusinessException("Clearing country not found: " +country+ " in binlist api");
        }

        Optional<ClearingCost> cost = clearingCostCrudService.getClearingCostByCountryId(country)
                .or(() -> clearingCostCrudService.getClearingCostByCountryId(DEFAULT_COUNTRY));

        return cost
                .map(clearingCost -> new BinCountryCost(country, clearingCost.getClearingCost()))
                .orElseThrow(() -> new BusinessException("Clearing country costs not found for country '" + country + "' or default 'Others'"));
    }
}
