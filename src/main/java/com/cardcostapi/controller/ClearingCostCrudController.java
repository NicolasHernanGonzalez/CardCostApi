package com.cardcostapi.controller;

import com.cardcostapi.domain.ClearingCost;
import com.cardcostapi.services.ClearingCostCrudServiceImpl;
import com.cardcostapi.utils.CountryValidator;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cost")
public class ClearingCostCrudController {

    private final ClearingCostCrudServiceImpl clearingCostService;

    public ClearingCostCrudController(ClearingCostCrudServiceImpl service) {
        this.clearingCostService = service;
    }

    @PostMapping("/{country}")
    public ResponseEntity<ClearingCost> createClearingCost(@PathVariable String country, @Valid @RequestBody ClearingCost clearingCost) {
        //TODO validar los costos que no sean negativos
        //CountryValidator.validateOrThrow(country);
        clearingCost.setCountry(country);
        ClearingCost response = clearingCostService.addClearingCost(clearingCost);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{country}")
    public ResponseEntity<ClearingCost> updateClearingCost(@PathVariable String country, @Valid @RequestBody ClearingCost clearingCost) {

        clearingCost.setCountry(country);
        ClearingCost response = clearingCostService.updateClearingCost(clearingCost);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{country}")
    public ResponseEntity<ClearingCost> getClearingCost(@Valid @PathVariable String country) {

        return clearingCostService.getClearingCostByCountryId(country)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{country}")
    public ResponseEntity<ClearingCost> deleteClearingCost(@Valid @PathVariable String country) {
        CountryValidator.validateOrThrow(country);
        this.clearingCostService.deleteClearingCost(country);
        return ResponseEntity.noContent().build();
    }
}