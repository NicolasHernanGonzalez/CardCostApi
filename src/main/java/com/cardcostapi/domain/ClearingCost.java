package com.cardcostapi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ClearingCost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @JsonIgnore
    private Long id;

    @NotNull(message = "Country is required")
    @JsonProperty("country")
    private String country;

    @NotNull(message = "Clearing cost is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Clearing cost must be greater than 0")
    @JsonProperty("cost")
    private Double clearingCost;
}
