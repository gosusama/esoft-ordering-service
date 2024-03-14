package com.esoft.report.service;

import com.esoft.report.service.dto.OrderSummaryReportDTO;
import com.esoft.report.service.repository.OrderReportRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderReportRepositoryUnitTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @InjectMocks
    private OrderReportRepositoryImpl orderReportRepository;

    @Test
    public void testCountByCreateUserId() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(1L);

        Long result = orderReportRepository.countByCreateUserId(1);

        assertEquals(1L, result);
        verify(entityManager).createNativeQuery(anyString());
        verify(query).setParameter(anyString(), anyInt());
        verify(query).getSingleResult();
    }

    @Test
    public void testSumAmountByUserId() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(new BigDecimal("100.00"));

        BigDecimal result = orderReportRepository.sumAmountByUserId(1);

        assertEquals(new BigDecimal("100.00"), result);
        verify(entityManager).createNativeQuery(anyString());
        verify(query).setParameter(anyString(), anyInt());
        verify(query).getSingleResult();
    }

    @Test
    public void testFindCountOrdersAndSumAmountByUid() {
        Object[] queryResult = new Object[]{1L, new BigDecimal("100.00")};
        when(entityManager.createNativeQuery(any(String.class))).thenReturn(query);
        when(query.getSingleResult()).thenReturn(queryResult);

        OrderSummaryReportDTO result = orderReportRepository.findCountOrdersAndSumAmountByUid(1);

        assertEquals(1L, result.getTotalOrders());
        assertEquals(new BigDecimal("100.00"), result.getTotalOrderValue());

        verify(entityManager).createNativeQuery(anyString());
        verify(query).setParameter(anyString(), anyInt());
        verify(query).getSingleResult();
    }

    @Test
    public void testFindCountOrdersAndSumAmount() {
        Object[] queryResult = new Object[]{1L, new BigDecimal("100.00")};

        when(entityManager.createNativeQuery(any(String.class))).thenReturn(query);
        when(query.getSingleResult()).thenReturn(queryResult);

        OrderSummaryReportDTO result = orderReportRepository.findCountOrdersAndSumAmount(2022, 1);

        assertEquals(1L, result.getTotalOrders());
        assertEquals(new BigDecimal("100.00"), result.getTotalOrderValue());

        verify(entityManager).createNativeQuery(anyString());
        verify(query, times(2)).setParameter(anyString(), anyInt());
        verify(query).getSingleResult();
    }
}