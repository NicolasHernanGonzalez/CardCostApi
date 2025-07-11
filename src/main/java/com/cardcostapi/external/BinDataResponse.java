package com.cardcostapi.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BinDataResponse {

    @JsonProperty("country")
    private Country country;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Country {
        @JsonProperty("alpha2")
        private String alpha2;
    }

    public String getCountryString() {
        return this.getCountry().getAlpha2();
    }

    public boolean isValid() {
        return this.getCountry() != null && this.getCountry().getAlpha2() != null;
    }

}
