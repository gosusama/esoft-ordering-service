package com.esoft.report.service.controller;

import com.esoft.common.config.response.ApiResponse;
import com.esoft.report.service.dto.OrderSummaryReportDTO;
import com.esoft.report.service.service.OrderReportService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
public class OrderReportController {
    private final OrderReportService orderReportService;

    public OrderReportController(OrderReportService orderReportService) {
        this.orderReportService = orderReportService;
    }

    @GetMapping("/reports/orders/summary/orders/{uid}")
    public ResponseEntity<ApiResponse<Long>> getOrderSummary(@PathVariable int uid) {
        ApiResponse<Long> response = new ApiResponse<>("success", orderReportService.countByCreateUserId(uid));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports/orders/summary/revenue/{uid}")
    public ResponseEntity<ApiResponse<BigDecimal>> getRevenueSummary(@PathVariable int uid) {
        ApiResponse<BigDecimal> response = new ApiResponse<>("success", orderReportService.sumAmountByUserId(uid));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports/orders/summary/orders-revenue")
    public ResponseEntity<ApiResponse<OrderSummaryReportDTO>> getOrdersAndRevenueSummary(
            @RequestParam() Integer year,
            @RequestParam(required = false) @Min(1) @Max(12) Integer month) {
        ApiResponse<OrderSummaryReportDTO> response = new ApiResponse<>(
                "success",
                orderReportService.getOrdersAndRevenueSummary(year, month)
        );

        return ResponseEntity.ok(response);
    }
}
