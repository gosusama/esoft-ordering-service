package com.esoft.order.service;

import com.esoft.common.config.response.OrderNotFoundException;
import com.esoft.order.service.dto.OrderDTO;
import com.esoft.order.service.entity.Order;
import com.esoft.order.service.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestPropertySource("/application-test.properties")
@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerIntegrationTest {
    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @Autowired
    ObjectMapper objectMapper;

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
    public void getOrderByIdHttpRequest() throws Exception {
        mockMvc.perform(get("/api/orders/3")
                        .with(httpBasic("customer", "customer")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code", is("1708868200717_3")));

        mockMvc.perform(get("/api/orders/3")
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code", is("1708868200717_3")));
    }

    @Test
    public void getOrderByIdHttpRequestNoPermissionExceptionResponse() throws Exception {
        mockMvc.perform(get("/api/orders/2")
                        .with(httpBasic("customer", "customer")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("User customer does not have permission to perform the operation.")));
    }

    @Test
    public void getOrderByIdHttpRequestOrderDoesNotExistResponse() throws Exception {
        mockMvc.perform(get("/api/orders/200")
                        .with(httpBasic("customer", "customer")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Order id not found - 200")));
    }

    @Test
    public void getOrdersByUidHttpRequest() throws Exception {
        mockMvc.perform(get("/api/orders/users/3")
                        .with(httpBasic("customer", "customer")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.totalPages", is(1)))
                .andExpect(jsonPath("$.data.totalElements", is(1)))
                .andExpect(jsonPath("$.data.last", is(true)));
    }

    @Test
    public void getOrdersByUidHttpRequestNoPermissionExceptionResponse() throws Exception {
        mockMvc.perform(get("/api/orders/users/300")
                        .with(httpBasic("customer", "customer")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("User customer does not have permission to perform the operation.")));
    }

    @Test
    public void createOrderHttpRequest() throws Exception {
        OrderDTO order = new OrderDTO("LUXURY", 1, "PHOTO_EDITING", new BigDecimal("113.13"));
        order.setDescription("Resize");
        order.setNote("Deadline is 2024-02-26");

        mockMvc.perform(post("/api/orders")
                        .with(httpBasic("customer", "customer"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.category", is("LUXURY")))
                .andExpect(jsonPath("$.data.quantity", is(1)))
                .andExpect(jsonPath("$.data.serviceName", is("PHOTO_EDITING")))
                .andExpect(jsonPath("$.data.amount", is(113.13)))
                .andExpect(jsonPath("$.data.description", is("Resize")))
                .andExpect(jsonPath("$.data.note", is("Deadline is 2024-02-26")));

        assertNotNull(orderService.findById(1));

        Order verifyOrder = orderService.findById(1);

        assertEquals("customer", verifyOrder.getCreateUser().getUsername());
    }

    @Test
    public void createOrderHttpRequestInvalidCategoryResponse() throws Exception {
        OrderDTO order = new OrderDTO("LUXURY213", 1, "PHOTO_EDITING", new BigDecimal("113.13"));

        mockMvc.perform(post("/api/orders")
                        .with(httpBasic("customer", "customer"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Invalid category")));


        assertThrows(OrderNotFoundException.class, () -> {
            orderService.findById(1);
        });
    }

    @Test
    public void createOrderHttpRequestInvalidQuantityResponse() throws Exception {
        OrderDTO order = new OrderDTO("LUXURY", -1, "PHOTO_EDITING", new BigDecimal("113.13"));

        mockMvc.perform(post("/api/orders")
                        .with(httpBasic("customer", "customer"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("quantity must be greater than 0")));


        assertThrows(OrderNotFoundException.class, () -> {
            orderService.findById(1);
        });
    }

    @Test
    public void createOrderHttpRequestInvalidServiceNameResponse() throws Exception {
        OrderDTO order = new OrderDTO("LUXURY", 1, "PHOTO_EDITING123", new BigDecimal("113.13"));

        mockMvc.perform(post("/api/orders")
                        .with(httpBasic("customer", "customer"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Invalid service name")));


        assertThrows(OrderNotFoundException.class, () -> {
            orderService.findById(1);
        });
    }

    @Test
    public void createOrderHttpRequestInvalidAmountResponse() throws Exception {
        OrderDTO order = new OrderDTO("LUXURY", 1, "PHOTO_EDITING", new BigDecimal("-113.13"));

        mockMvc.perform(post("/api/orders")
                        .with(httpBasic("customer", "customer"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("amount must be greater than 0")));


        assertThrows(OrderNotFoundException.class, () -> {
            orderService.findById(1);
        });
    }

    @Test
    public void updateOrderHttpRequest() throws Exception {
        assertNotNull(orderService.findById(3));

        OrderDTO order = new OrderDTO("LUXURY", 1, "PHOTO_EDITING", new BigDecimal("113.13"));
        order.setId(3);
        order.setDescription("Resize");
        order.setNote("Deadline is 2024-02-26");

        mockMvc.perform(put("/api/orders")
                        .with(httpBasic("customer", "customer"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.category", is("LUXURY")))
                .andExpect(jsonPath("$.data.quantity", is(1)))
                .andExpect(jsonPath("$.data.serviceName", is("PHOTO_EDITING")))
                .andExpect(jsonPath("$.data.amount", is(113.13)))
                .andExpect(jsonPath("$.data.description", is("Resize")))
                .andExpect(jsonPath("$.data.note", is("Deadline is 2024-02-26")));

        Order verifyOrder = orderService.findById(3);
        assertEquals("LUXURY", verifyOrder.getCategory());
        assertEquals(1, verifyOrder.getQuantity());
        assertEquals("PHOTO_EDITING", verifyOrder.getServiceName());
        assertEquals(new BigDecimal("113.13"), verifyOrder.getAmount());
        assertEquals("Resize", verifyOrder.getDescription());
        assertEquals("Deadline is 2024-02-26", verifyOrder.getNote());
    }

    @Test
    public void updateOrderHttpRequestNoPermissionExceptionResponse() throws Exception {
        assertNotNull(orderService.findById(3));

        OrderDTO order = new OrderDTO("LUXURY", 1, "PHOTO_EDITING", new BigDecimal("113.13"));
        order.setId(3);
        order.setDescription("Resize");
        order.setNote("Deadline is 2024-02-26");

        mockMvc.perform(put("/api/orders")
                        .with(httpBasic("admin", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("User admin does not have permission to perform the operation.")));

        Order verifyOrder = orderService.findById(3);
        assertEquals("SUPER_LUXURY", verifyOrder.getCategory());
        assertEquals(2, verifyOrder.getQuantity());
        assertEquals("VIDEO_EDITING", verifyOrder.getServiceName());
        assertEquals(new BigDecimal("150.50"), verifyOrder.getAmount());
        assertNull(verifyOrder.getDescription());
        assertNull(verifyOrder.getNote());
    }

    @Test
    public void deleteOrderHttpRequest() throws Exception {
        assertDoesNotThrow(() -> {
            orderService.findById(3);
        });

        mockMvc.perform(delete("/api/orders/3")
                        .with(httpBasic("customer", "customer")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Delete order id - 3")));


        assertThrows(OrderNotFoundException.class, () -> {
            orderService.findById(3);
        });
    }

    @Test
    public void deleteOrderHttpRequestDoesNotExistResponse() throws Exception {
        assertThrows(OrderNotFoundException.class, () -> {
            orderService.findById(1);
        });

        mockMvc.perform(delete("/api/orders/1")
                        .with(httpBasic("customer", "customer")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Order id not found - 1")));
    }

    @Test
    public void deleteOrderHttpRequestNoPermissionExceptionResponse() throws Exception {
        assertNotNull(orderService.findById(3));

        mockMvc.perform(delete("/api/orders/3")
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("User admin does not have permission to perform the operation.")));

        assertNotNull(orderService.findById(3));
    }

    @AfterEach
    public void setupAfterTransaction() {
        jdbc.execute(sqlDeleteOrders);
        jdbc.execute(sqlDropTableAuthorities);
        jdbc.execute(sqlDeleteUsers);
    }
}
