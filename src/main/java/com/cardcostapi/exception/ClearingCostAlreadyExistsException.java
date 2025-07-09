package com.cardcostapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ClearingCostAlreadyExistsException extends BusinessException{
    public ClearingCostAlreadyExistsException(String country, String cost) {
        super("Clearing cost " + cost + " for country " +country+ " already exists.");
    }

}


