package com.cardcostapi.services;

import com.cardcostapi.domain.ClearingCost;
import com.cardcostapi.exception.ClearingCostNotFoundException;
import com.cardcostapi.repository.ClearingCostServiceRepository;
import com.cardcostapi.repository.IClearingCostRepository;
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
    private IClearingCostRepository repository;

    @InjectMocks
    private ClearingCostCrudServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void ClearingCostCrudServiceImpl_AddClearingCost_Success() {
        //SETUP
        ClearingCost cost = new ClearingCost();
        cost.setCountry("AR");
        cost.setClearingCost(10.5);

        //MOCK
        when(repository.getClearingCostByCountry("AR")).thenReturn(Collections.emptyList());
        when(repository.save(cost)).thenReturn(cost);

        //SUT
        ClearingCost result = service.addClearingCost(cost);

        //ASSERT
        assertNotNull(result);
        assertEquals("AR", result.getCountry());
        assertEquals(10.5, result.getClearingCost());
        verify(repository).save(cost);
    }

    @Test
    public void ClearingCostCrudServiceImpl_AddClearingCost_AlreadyExists_ThrowsException() {
        //SETUP
        ClearingCost cost = new ClearingCost();
        cost.setCountry("AR");
        cost.setClearingCost(10.5);

        //MOCK
        when(repository.getClearingCostByCountry("AR")).thenReturn(List.of(cost));

        //ASSERT
        assertThrows(DataIntegrityViolationException.class, () -> {
            service.addClearingCost(cost);
        });

        verify(repository, never()).save(any());
    }

    @Test
    public void ClearingCostCrudServiceImpl_UpdateClearingCost_Success() {
        //SETUP
        ClearingCost costToUpdate = new ClearingCost();
        costToUpdate.setCountry("BR");
        costToUpdate.setClearingCost(20.0);

        ClearingCost existing = new ClearingCost();
        existing.setCountry("BR");
        existing.setClearingCost(15.0);

        //MOCK
        when(repository.findByCountry("BR")).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        //SUT
        ClearingCost result = service.updateClearingCost(costToUpdate);

        //ASSERT
        assertNotNull(result);
        assertEquals("BR", result.getCountry());
        assertEquals(20.0, result.getClearingCost());
        verify(repository).save(existing);
    }

    @Test
    public void ClearingCostCrudServiceImpl_UpdateClearingCost_NotFound_ThrowsException() {
        //SETUP
        ClearingCost cost = new ClearingCost();
        cost.setCountry("UY");
        cost.setClearingCost(12.0);

        //MOCK
        when(repository.findByCountry("UY")).thenReturn(Optional.empty());


        //ASSERT
        assertThrows(ClearingCostNotFoundException.class, () -> {
            service.updateClearingCost(cost);
        });

        verify(repository, never()).save(any());
    }
}
