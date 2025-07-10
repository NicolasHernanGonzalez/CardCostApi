package com.cardcostapi.controller;

import com.cardcostapi.domain.ClearingCost;
import com.cardcostapi.services.ClearingCostCrudServiceImpl;
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

    @PostMapping
    public ResponseEntity<ClearingCost> createClearingCost(@Valid @RequestBody ClearingCost clearingCost) {
        ClearingCost response = clearingCostService.addClearingCost(clearingCost);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClearingCost> updateClearingCost(@PathVariable Long id, @Valid @RequestBody ClearingCost clearingCost) {
        clearingCost.setId(id);
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
    public ResponseEntity<Void> deleteClearingCost(@Valid @PathVariable String country) {
        clearingCostService.deleteClearingCost(country);
        return ResponseEntity.noContent().build();
    }
}
