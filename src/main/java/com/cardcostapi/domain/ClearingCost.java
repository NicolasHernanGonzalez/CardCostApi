package com.cardcostapi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;


@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ClearingCost {

    @Id
    @Setter
    @Getter
    @JsonProperty("country")
    private String country;

    @NotNull(message = "Clearing cost is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Clearing cost must be greater than 0")
    @JsonProperty("cost")
    @Setter
    @Getter
    private Double clearingCost;

}
