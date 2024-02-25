package com.esoft.report.service.repository;

import com.esoft.common.config.entity.Order;
import com.esoft.report.service.dto.OrderSummaryReportDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface OrderReportRepository {
    Long countByCreateUserId(int uid);

    BigDecimal sumAmountByUserId(int uid);

    OrderSummaryReportDTO findCountOrdersAndSumAmount(Integer year, Integer month);
}
