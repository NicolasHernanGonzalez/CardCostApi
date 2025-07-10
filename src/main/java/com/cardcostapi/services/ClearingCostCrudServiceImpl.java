package com.cardcostapi.services;

import com.cardcostapi.domain.ClearingCost;
import com.cardcostapi.exception.ClearingCostNotFoundException;
import com.cardcostapi.repository.IClearingCostRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClearingCostCrudServiceImpl implements IClearingCostCrudService {

    private final IClearingCostRepository clearingCostRepository;

    public ClearingCostCrudServiceImpl(IClearingCostRepository repository) {
        this.clearingCostRepository = repository;
    }

    @Override
    public ClearingCost addClearingCost(ClearingCost clearingCost) {
        List<ClearingCost> existing = clearingCostRepository.getClearingCostByCountry(clearingCost.getCountry());
        if (existing.isEmpty()) {
            return clearingCostRepository.save(clearingCost);
        }
        throw new DataIntegrityViolationException("Clearing cost already exists for the country: " + clearingCost.getCountry());
    }

    @Override
    public void deleteClearingCost(String countryId) {
        clearingCostRepository.deleteByCountry(countryId);
    }

    @Override
    public Optional<ClearingCost> getClearingCostByCountryId(String countryId) {
        return clearingCostRepository.findByCountry(countryId);
    }

    @Override
    public ClearingCost updateClearingCost(ClearingCost clearingCost) {
        Optional<ClearingCost> existing = clearingCostRepository.findByCountry(clearingCost.getCountry());
        if (existing.isPresent()) {
            ClearingCost saved = existing.get();
            saved.setClearingCost(clearingCost.getClearingCost());
            return clearingCostRepository.save(saved);
        }
        throw new ClearingCostNotFoundException(clearingCost.getCountry());
    }
}
