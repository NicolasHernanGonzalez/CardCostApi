package com.cardcostapi.repository;

import com.cardcostapi.domain.ClearingCost;

import java.util.List;
import java.util.Optional;

public interface IClearingCostRepository {
    Optional<ClearingCost> findByCountry(String country);
    void deleteByCountry(String country);
    List<ClearingCost> getClearingCostByCountry(String country);
    ClearingCost save(ClearingCost cost);
}