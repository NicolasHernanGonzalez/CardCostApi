package com.cardcostapi.services;

import com.cardcostapi.domain.ClearingCost;
import com.cardcostapi.exception.ClearingCostNotFoundException;
import com.cardcostapi.repository.ClearingCostServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClearingCostCrudServiceImplTest {

    @Mock
    private ClearingCostServiceRepository repository;

    @InjectMocks
    private ClearingCostCrudServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void ClearingCostCrudServiceImpl_AddClearingCost_Success() {
        ClearingCost cost = new ClearingCost();
        cost.setCountry("AR");
        cost.setClearingCost(10.5);

        when(repository.getClearingCostByCountry("AR")).thenReturn(Collections.emptyList());
        when(repository.save(cost)).thenReturn(cost);

        ClearingCost result = service.addClearingCost(cost);

        assertNotNull(result);
        assertEquals("AR", result.getCountry());
        assertEquals(10.5, result.getClearingCost());
        verify(repository).save(cost);
    }

    @Test
    public void ClearingCostCrudServiceImpl_AddClearingCost_AlreadyExists_ThrowsException() {
        ClearingCost cost = new ClearingCost();
        cost.setCountry("AR");
        cost.setClearingCost(10.5);

        when(repository.getClearingCostByCountry("AR")).thenReturn(List.of(cost));

        assertThrows(DataIntegrityViolationException.class, () -> {
            service.addClearingCost(cost);
        });

        verify(repository, never()).save(any());
    }

    @Test
    public void ClearingCostCrudServiceImpl_UpdateClearingCost_Success() {
        ClearingCost costToUpdate = new ClearingCost();
        costToUpdate.setCountry("BR");
        costToUpdate.setClearingCost(20.0);

        ClearingCost existing = new ClearingCost();
        existing.setCountry("BR");
        existing.setClearingCost(15.0);

        when(repository.findByCountry("BR")).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        ClearingCost result = service.updateClearingCost(costToUpdate);

        assertNotNull(result);
        assertEquals("BR", result.getCountry());
        assertEquals(20.0, result.getClearingCost());
        verify(repository).save(existing);
    }

    @Test
    public void ClearingCostCrudServiceImpl_UpdateClearingCost_NotFound_ThrowsException() {
        ClearingCost cost = new ClearingCost();
        cost.setCountry("UY");
        cost.setClearingCost(12.0);

        when(repository.findByCountry("UY")).thenReturn(Optional.empty());

        assertThrows(ClearingCostNotFoundException.class, () -> {
            service.updateClearingCost(cost);
        });

        verify(repository, never()).save(any());
    }
}
