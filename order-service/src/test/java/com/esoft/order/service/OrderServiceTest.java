package com.esoft.order.service;

import com.esoft.common.config.entity.Order;
import com.esoft.common.config.entity.User;
import com.esoft.common.config.repository.UserRepository;
import com.esoft.order.service.service.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application-test.properties")
@SpringBootTest
public class OrderServiceTest {
    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Value("${sql.scripts.create.users}")
    private String sqlAddUsers;

    @Value("${sql.scripts.create.orders}")
    private String sqlAddOrders;

    @Value("${sql.scripts.delete.users}")
    private String sqlDeleteUsers;

    @Value("${sql.scripts.delete.orders}")
    private String sqlDeleteOrders;

    @BeforeEach
    public void setupDatabase() {
        jdbc.execute(sqlAddUsers);
        jdbc.execute(sqlAddOrders);
    }

    @Test
    public void findByIdService() {
        Order order = orderService.findById(2);

        assertEquals("1708867969268_2", order.getCode());
    }

    @Test
    public void findByNotExistIdService() {
        assertNull(orderService.findById(200));
    }

    @Test
    @Transactional
    public void createOrderService() {
        User user = userRepository.findByUsername("customer");
        Order order = new Order("LUXURY", 1, "PHOTO_EDITING", new BigDecimal("113.13"));
        order.setDescription("Resize");
        order.setNote("Deadline is 2024-02-26");
        order.setCreateUser(user);

        Order savedOrder = orderService.save(order);

        assertEquals(1, savedOrder.getId());
        assertEquals("LUXURY", savedOrder.getCategory());
        assertEquals(1, savedOrder.getQuantity());
        assertEquals("PHOTO_EDITING", savedOrder.getServiceName());
        assertEquals(new BigDecimal("113.13"), savedOrder.getAmount());
        assertEquals("Resize", savedOrder.getDescription());
        assertEquals("Deadline is 2024-02-26", savedOrder.getNote());
    }

    @Test
    public void updateOrderService() {
        assertDoesNotThrow(() -> {
            orderService.findById(2);
        });

        Order order = orderService.findById(2);

        assertEquals("LUXURY", order.getCategory());
        assertEquals(1, order.getQuantity());
        assertEquals("PHOTO_EDITING", order.getServiceName());
        assertEquals(new BigDecimal("150.00"), order.getAmount());
        assertNull(order.getDescription());
        assertNull(order.getNote());

        order.setCategory("SUPER_LUXURY");
        order.setQuantity(2);
        order.setServiceName("VIDEO_EDITING");
        order.setAmount(new BigDecimal("150.2"));
        order.setDescription("Resize");
        order.setNote("Deadline is 2024-02-26");

        Order savedOrder = orderService.save(order);

        assertEquals("SUPER_LUXURY", savedOrder.getCategory());
        assertEquals(2, savedOrder.getQuantity());
        assertEquals("VIDEO_EDITING", savedOrder.getServiceName());
        assertEquals(new BigDecimal("150.2"), savedOrder.getAmount());
        assertEquals("Resize", savedOrder.getDescription());
        assertEquals("Deadline is 2024-02-26", savedOrder.getNote());
    }

    @Test
    public void deleteByIdService() {
        assertNotNull(orderService.findById(2));

        orderService.deleteById(2);

        assertNull(orderService.findById(2));
    }

    @AfterEach
    public void setupAfterTransaction() {
        jdbc.execute(sqlDeleteOrders);
        jdbc.execute(sqlDeleteUsers);
    }
}
