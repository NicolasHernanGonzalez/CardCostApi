package com.cardcostapi.services;

import java.util.concurrent.ExecutionException;

public interface IBinLookupService {
    public String getCountryByBin(String pan);
}
