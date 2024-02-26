package com.esoft.report.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application-test.properties")
@SpringBootTest
@AutoConfigureMockMvc
public class OrderReportControllerTest {
    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    private MockMvc mockMvc;
    @Value("${sql.scripts.create.users}")
    private String sqlAddUsers;

    @Value("${sql.scripts.create.table.authorities}")
    private String sqlCreateTableAuthorities;

    @Value("${sql.scripts.create.authorities}")
    private String sqlAddAuthorities;

    @Value("${sql.scripts.create.orders}")
    private String sqlAddOrders;

    @Value("${sql.scripts.delete.users}")
    private String sqlDeleteUsers;

    @Value("${sql.scripts.drop.table.authorities}")
    private String sqlDropTableAuthorities;

    @Value("${sql.scripts.delete.orders}")
    private String sqlDeleteOrders;

    @BeforeEach
    public void setupDatabase() {
        jdbc.execute(sqlAddUsers);
        jdbc.execute(sqlCreateTableAuthorities);
        jdbc.execute(sqlAddAuthorities);
        jdbc.execute(sqlAddOrders);
    }

    @Test
    public void getOrderSummaryHttpRequest() throws Exception {
        mockMvc.perform(get("/api/reports/orders/summary/orders/3")
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(2)));
    }

    @Test
    public void getOrderSummaryHttpRequestUnauthorizedResponse() throws Exception {
        mockMvc.perform(get("/api/reports/orders/summary/orders/3")
                        .with(httpBasic("custom", "custom")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getOrderSummaryDoesNotExistUserResponse() throws Exception {
        mockMvc.perform(get("/api/reports/orders/summary/orders/-1")
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(0)));
    }

    @Test
    public void getRevenueSummaryHttpRequest() throws Exception {
        mockMvc.perform(get("/api/reports/orders/summary/revenue/3")
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(361.0)));
    }

    @Test
    public void getRevenueSummaryHttpRequestUnauthorizedResponse() throws Exception {
        mockMvc.perform(get("/api/reports/orders/summary/revenue/3")
                        .with(httpBasic("custom", "custom")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getRevenueSummaryDoesNotExistUserResponse() throws Exception {
        mockMvc.perform(get("/api/reports/orders/summary/revenue/-1")
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(nullValue())));
    }

    @Test
    public void getOrdersAndRevenueSummaryByUidHttpRequest() throws Exception {
        mockMvc.perform(get("/api/reports/orders/summary/orders-revenue/users/3")
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalOrders", is(2)))
                .andExpect(jsonPath("$.data.totalOrderValue", is(361.0)));
    }

    @Test
    public void getOrdersAndRevenueSummaryByUidHttpRequestUnauthorizedResponse() throws Exception {
        mockMvc.perform(get("/api/reports/orders/summary/orders-revenue/users/3")
                        .with(httpBasic("custom", "custom")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getOrdersAndRevenueSummaryByUidDoesNotExistUserResponse() throws Exception {
        mockMvc.perform(get("/api/reports/orders/summary/orders-revenue/users/-1")
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalOrders", is(0)))
                .andExpect(jsonPath("$.data.totalOrderValue", is(nullValue())));
    }

    @Test
    public void getOrdersAndRevenueSummaryHttpRequest() throws Exception {
        mockMvc.perform(get("/api/reports/orders/summary/orders-revenue?year=2024&month=2")
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalOrders", is(6)))
                .andExpect(jsonPath("$.data.totalOrderValue", is(1161.0)));

        mockMvc.perform(get("/api/reports/orders/summary/orders-revenue?year=2024")
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalOrders", is(6)))
                .andExpect(jsonPath("$.data.totalOrderValue", is(1161.0)));
    }

    @Test
    public void getOrdersAndRevenueSummaryHttpRequestUnauthorizedResponse() throws Exception {
        mockMvc.perform(get("/api/reports/orders/summary/orders-revenue/users/3")
                        .with(httpBasic("custom", "custom")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getOrdersAndRevenueSummaryWithoutYearResponse() throws Exception {
        mockMvc.perform(get("/api/reports/orders/summary/orders-revenue")
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getOrdersAndRevenueSummaryWithInvalidYearResponse() throws Exception {
        mockMvc.perform(get("/api/reports/orders/summary/orders-revenue?year=-1&month=1")
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getOrdersAndRevenueSummaryWithInvalidMonthResponse() throws Exception {
        mockMvc.perform(get("/api/reports/orders/summary/orders-revenue?year=2024&month=0")
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/reports/orders/summary/orders-revenue?year=2024&month=13")
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isBadRequest());
    }

    @AfterEach
    public void setupAfterTransaction() {
        jdbc.execute(sqlDeleteOrders);
        jdbc.execute(sqlDropTableAuthorities);
        jdbc.execute(sqlDeleteUsers);
    }
}
