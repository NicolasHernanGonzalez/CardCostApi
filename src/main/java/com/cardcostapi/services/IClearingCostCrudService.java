package com.cardcostapi.services;

import com.cardcostapi.domain.ClearingCost;

import java.util.Optional;

public interface IClearingCostCrudService {

    public Optional<ClearingCost> getClearingCostByCountryId(String countryId);

    public ClearingCost addClearingCost(ClearingCost clearingCost);

    public void deleteClearingCost(String clearingCost);

    public ClearingCost updateClearingCost(ClearingCost clearingCost);

}
