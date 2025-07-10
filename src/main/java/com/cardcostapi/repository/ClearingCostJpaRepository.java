package com.cardcostapi.repository;

import com.cardcostapi.domain.ClearingCost;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ClearingCostJpaRepository implements IClearingCostRepository {

    private final ClearingCostServiceRepository jpaRepository;

    public ClearingCostJpaRepository(ClearingCostServiceRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<ClearingCost> findByCountry(String country) {
        return jpaRepository.findByCountry(country);
    }

    @Override
    public void deleteByCountry(String country) {
        jpaRepository.deleteByCountry(country);
    }

    @Override
    public List<ClearingCost> getClearingCostByCountry(String country) {
        return jpaRepository.getClearingCostByCountry(country);
    }

    @Override
    public ClearingCost save(ClearingCost cost) {
        return jpaRepository.save(cost);
    }
}
