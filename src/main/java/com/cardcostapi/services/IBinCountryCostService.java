package com.cardcostapi.services;


import com.cardcostapi.controller.response.BinCountryCost;

public interface IBinCountryCostService {

    public BinCountryCost getClearingCostByBin(String bin);

}
