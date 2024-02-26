package com.esoft.report.service.repository;

import com.esoft.report.service.dto.OrderSummaryReportDTO;

import java.math.BigDecimal;

public interface OrderReportRepository {
    Long countByCreateUserId(int uid);

    BigDecimal sumAmountByUserId(int uid);

    OrderSummaryReportDTO findCountOrdersAndSumAmountByUid(int uid);

    OrderSummaryReportDTO findCountOrdersAndSumAmount(Integer year, Integer month);
}
