package com.esoft.report.service.dto;

import java.math.BigDecimal;

public class OrderSummaryReportDTO {
    private Long totalOrders;

    private BigDecimal totalOrderValue;

    public OrderSummaryReportDTO() {
    }

    public OrderSummaryReportDTO(Long totalOrders, BigDecimal totalOrderValue) {
        this.totalOrders = totalOrders;
        this.totalOrderValue = totalOrderValue;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public BigDecimal getTotalOrderValue() {
        return totalOrderValue;
    }

    public void setTotalOrderValue(BigDecimal totalOrderValue) {
        this.totalOrderValue = totalOrderValue;
    }
}
