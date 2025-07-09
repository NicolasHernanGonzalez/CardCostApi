package com.cardcostapi.exception;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String param) {
        super("Invalid param: " + param);
    }
}