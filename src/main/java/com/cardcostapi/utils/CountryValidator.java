package com.cardcostapi.utils;

import com.cardcostapi.exception.InvalidRequestException;

public class CountryValidator {

    private static final String COUNTRY_CODE_REGEX = "^[A-Za-z]{2}$";

    public static void validateOrThrow(String countryCode) {
        if (!countryCode.matches(COUNTRY_CODE_REGEX)) {
            throw new InvalidRequestException(countryCode);
        }
    }

}
