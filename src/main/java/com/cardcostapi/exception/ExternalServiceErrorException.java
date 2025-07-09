package com.cardcostapi.exception;

public class ExternalServiceErrorException extends RuntimeException  {
    public ExternalServiceErrorException(String s) {
        super(s);
    }
}
