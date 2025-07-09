package com.cardcostapi.domain;

import com.cardcostapi.exception.InvalidRequestException;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PanNumber {

    public static final int binSize = 8;

    @JsonProperty("card_number")
    private String cardNumber;

    public String calculateBinNumber() {
        return cardNumber.substring(0, binSize);
    }

    public void validate() {
        if (this.getCardNumber() == null) {
            throw new InvalidRequestException("PAN is required");
        }

        String cardNumber = this.getCardNumber().trim();

        if (cardNumber.length() < 8 || cardNumber.length() > 19) {
            throw new InvalidRequestException("PAN length must be between 8 and 19 digits");
        }

        if (!cardNumber.matches("\\d+")) {
            throw new InvalidRequestException("PAN must contain digits only");
        }
    }
}
