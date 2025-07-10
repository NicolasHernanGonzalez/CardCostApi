package com.cardcostapi.controller;

import com.cardcostapi.controller.response.BinCountryCost;
import com.cardcostapi.domain.PanNumber;
import com.cardcostapi.exception.TooManyRequestsException;
import com.cardcostapi.services.IBinCountryCostService;
import com.cardcostapi.services.IBinLookupService;
import com.cardcostapi.services.IRateLimitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/payment-cards-cost")
public class BinDataController {

    private final IBinCountryCostService binCountryCostService;


    public BinDataController(IBinCountryCostService binCountryCostService,IRateLimitService rateLimitService) {
        this.binCountryCostService = binCountryCostService;
    }

    @PostMapping
    public ResponseEntity<BinCountryCost> getBinClearingCost(@RequestBody PanNumber pan) {

        pan.validate();

        String bin = pan.calculateBinNumber();

        BinCountryCost clearingCostByBin = binCountryCostService.getClearingCostByBin(bin);

        return new ResponseEntity<>(clearingCostByBin, HttpStatus.OK);
    }

}
