package com.esoft.report.service.service;


import com.esoft.report.service.dto.OrderSummaryReportDTO;

import java.math.BigDecimal;

public interface OrderReportService {
    Long countByCreateUserId(int uid);

    BigDecimal sumAmountByUserId(int uid);

    OrderSummaryReportDTO getOrdersAndRevenueSummary(Integer year, Integer month);
}
