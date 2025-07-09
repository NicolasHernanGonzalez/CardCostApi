package com.cardcostapi.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class BinCountryCost {

    @JsonProperty("country")
    private String country;

    @JsonProperty("cost")
    private Double cost;

}
