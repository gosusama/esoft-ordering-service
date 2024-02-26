package com.esoft.report.service.service;

import com.esoft.report.service.dto.OrderSummaryReportDTO;
import com.esoft.report.service.repository.OrderReportRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderReportServiceImpl implements OrderReportService {
    private final OrderReportRepository orderReportRepository;

    public OrderReportServiceImpl(OrderReportRepository orderReportRepository) {
        this.orderReportRepository = orderReportRepository;
    }

    @Override
    public Long countByCreateUserId(int uid) {
        return orderReportRepository.countByCreateUserId(uid);
    }

    @Override
    public BigDecimal sumAmountByUserId(int uid) {
        return orderReportRepository.sumAmountByUserId(uid);
    }

    @Override
    public OrderSummaryReportDTO getOrdersAndRevenueSummaryByUid(int uid) {
        return orderReportRepository.findCountOrdersAndSumAmountByUid(uid);
    }

    @Override
    public OrderSummaryReportDTO getOrdersAndRevenueSummary(Integer year, Integer month) {
        return orderReportRepository.findCountOrdersAndSumAmount(year, month);
    }
}
