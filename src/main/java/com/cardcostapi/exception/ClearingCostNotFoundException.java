package com.cardcostapi.exception;

public class ClearingCostNotFoundException extends BusinessException{

    public ClearingCostNotFoundException(String country) {
        super("Clearing cost for country " +country+ " do not exists.");
    }
}
