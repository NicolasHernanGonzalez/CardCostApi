package com.cardcostapi.repository;

import com.cardcostapi.domain.ClearingCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClearingCostServiceRepository extends JpaRepository<ClearingCost, Long> {

    Optional<ClearingCost> findByCountry(String country);

    void deleteByCountry(String country);

    List<ClearingCost> getClearingCostByCountry(String country);
}
