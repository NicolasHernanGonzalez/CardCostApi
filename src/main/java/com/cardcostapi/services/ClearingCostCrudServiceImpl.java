package com.cardcostapi.services;

import com.cardcostapi.domain.ClearingCost;
import com.cardcostapi.exception.ClearingCostNotFoundException;
import com.cardcostapi.repository.ClearingCostServiceRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ClearingCostCrudServiceImpl implements IClearingCostCrudService {

    private final ClearingCostServiceRepository clearingCostServiceRepository;

    public ClearingCostCrudServiceImpl(ClearingCostServiceRepository repository) {
        this.clearingCostServiceRepository = repository;
    }

    @Override
    public ClearingCost addClearingCost(ClearingCost clearingCost) {
        //Validaciones
        List<ClearingCost> clearingCostByCountry = clearingCostServiceRepository.getClearingCostByCountry(clearingCost.getCountry());
        if (clearingCostByCountry.isEmpty()) {
            return this.clearingCostServiceRepository.save(clearingCost);
        }
        throw new DataIntegrityViolationException("Clearing cost already exists for the country: " + clearingCost.getCountry());
    }

    @Override
    public void deleteClearingCost(String countryId) {
       clearingCostServiceRepository.deleteByCountry(countryId);
    }

    @Override
    public Optional<ClearingCost> getClearingCostByCountryId(String countryId) {
        return this.clearingCostServiceRepository.findByCountry(countryId);
    }

    @Override
    public ClearingCost updateClearingCost(ClearingCost clearingCost) {
        //Validaciones
        Optional<ClearingCost> byCountry = this.clearingCostServiceRepository.findByCountry(clearingCost.getCountry());
        if (byCountry.isPresent()) {
            ClearingCost savedfClearingCost = byCountry.get();
            savedfClearingCost.setClearingCost(clearingCost.getClearingCost());
            return this.clearingCostServiceRepository.save(savedfClearingCost);
        }
        throw new ClearingCostNotFoundException(clearingCost.getCountry());
    }

}