package com.esoft.order.service;

import com.esoft.common.config.entity.User;
import com.esoft.common.config.response.OrderNotFoundException;
import com.esoft.order.service.entity.Order;
import com.esoft.order.service.repository.OrderRepository;
import com.esoft.order.service.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceUnitTest {
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;

    private User user;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(1);
        user.setUsername("customer");

        order = new Order();
        order.setId(1);
        order.setCode("123456_1");
        order.setCategory("LUXURY");
        order.setQuantity(1);
        order.setServiceName("PHOTO_EDITING");
        order.setAmount(new BigDecimal("113.13"));
        order.setDescription("Resize");
        order.setNote("Deadline is 2024-02-26");
        order.setCreateUser(user);
        order.setCreateDate(LocalDateTime.now());
    }

    @Test
    public void findByIdTest() {
        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(order));

        Order result = orderService.findById(1);

        assertEquals(order, result);

        verify(orderRepository).findById(anyInt());
    }

    @Test
    public void findByIdNotFoundTest() {
        when(orderRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.findById(1));

        verify(orderRepository).findById(anyInt());
    }

    @Test
    public void findByUidTest() {
        Page<Order> page = mock(Page.class);
        when(orderRepository.findByCreateUserId(anyInt(), any(Pageable.class))).thenReturn(page);

        Page<Order> result = orderService.findByUid(1, Pageable.unpaged());

        assertEquals(page, result);

        verify(orderRepository).findByCreateUserId(anyInt(), any(Pageable.class));
    }

    @Test
    public void saveTest() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order newOrder = new Order();
        newOrder.setCreateUser(user);
        Order result = orderService.save(newOrder);

        assertEquals(order, result);

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    public void deleteByIdTest() {
        doNothing().when(orderRepository).deleteById(anyInt());

        assertDoesNotThrow(() -> orderService.deleteById(1));

        verify(orderRepository).deleteById(anyInt());
    }

    @Test
    public void generateUniqueCodeTest() {
        int userId = 1;
        String code = orderService.generateUniqueCode(userId);

        assertTrue(code.endsWith("_" + userId));
    }
}
