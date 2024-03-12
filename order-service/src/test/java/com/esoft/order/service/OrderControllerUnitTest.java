package com.esoft.order.service;

import com.esoft.common.config.entity.User;
import com.esoft.common.config.response.ApiResponse;
import com.esoft.common.config.response.OrderNotFoundException;
import com.esoft.common.config.service.UserService;
import com.esoft.order.service.controller.OrderController;
import com.esoft.order.service.dto.OrderDTO;
import com.esoft.order.service.entity.Order;
import com.esoft.order.service.mapper.OrderMapper;
import com.esoft.order.service.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import javax.naming.NoPermissionException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderControllerUnitTest {

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderController orderController;

    private Order order;
    private User user;
    private OrderDTO orderDTO;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(1);
        user.setUsername("customer");

        order = new Order();
        order.setId(1);
        order.setCreateUser(user);

        orderDTO = new OrderDTO();
        orderDTO.setId(1);
    }

    @Test
    public void getOrderTest() throws Exception {
        when(userService.getCurrentUser()).thenReturn(user);
        when(orderService.findById(1)).thenReturn(order);
        when(orderMapper.entityToDTO(order)).thenReturn(orderDTO);

        ResponseEntity<ApiResponse<OrderDTO>> response = orderController.getOrder(1);

        assertEquals(orderDTO, response.getBody().getData());

        verify(userService).getCurrentUser();
        verify(orderService).findById(1);
        verify(orderMapper).entityToDTO(order);
    }

    @Test
    public void getOrderNotFoundTest() {
        int orderId = 1;
        when(orderService.findById(orderId)).thenThrow(OrderNotFoundException.class);

        assertThrows(OrderNotFoundException.class, () -> orderController.getOrder(orderId));

        verify(orderService).findById(orderId);
    }

    @Test
    public void getOrderNoPermissionTest() {
        User otherUser = new User();
        otherUser.setId(2);
        otherUser.setUsername("other");

        when(orderService.findById(1)).thenReturn(order);
        when(userService.getCurrentUser()).thenReturn(otherUser);
        when(userService.isAdmin()).thenReturn(false);

        assertThrows(NoPermissionException.class, () -> orderController.getOrder(1));

        verify(orderService).findById(1);
        verify(userService).getCurrentUser();
        verify(userService).isAdmin();
    }

    @Test
    public void getOrdersByUidTest() throws Exception {
        when(userService.getCurrentUser()).thenReturn(user);
        Page<Order> page = new PageImpl<>(Collections.singletonList(order));
        when(orderService.findByUid(1, PageRequest.of(0, 10))).thenReturn(page);
        when(orderMapper.entityToDTO(order)).thenReturn(orderDTO);

        ResponseEntity<ApiResponse<Page<OrderDTO>>> response = orderController.getOrdersByUid(1, 0, 10);

        assertEquals(orderDTO, response.getBody().getData().getContent().get(0));

        verify(userService).getCurrentUser();
        verify(orderService).findByUid(1, PageRequest.of(0, 10));
        verify(orderMapper).entityToDTO(order);
    }

    @Test
    public void getOrdersByUidNoPermissionTest() {
        User otherUser = new User();
        otherUser.setId(2);
        otherUser.setUsername("other");

        when(userService.getCurrentUser()).thenReturn(user);
        when(userService.isAdmin()).thenReturn(false);

        assertThrows(NoPermissionException.class, () -> orderController.getOrdersByUid(otherUser.getId(), 0, 10));

        verify(userService).getCurrentUser();
        verify(userService).isAdmin();
    }

    @Test
    public void addOrderTest() {
        when(orderMapper.dtoToEntity(orderDTO)).thenReturn(order);
        when(orderService.save(order)).thenReturn(order);
        when(orderMapper.entityToDTO(order)).thenReturn(orderDTO);

        ResponseEntity<ApiResponse<OrderDTO>> response = orderController.addOrder(orderDTO);

        assertEquals(orderDTO, response.getBody().getData());

        verify(orderMapper).dtoToEntity(orderDTO);
        verify(orderService).save(order);
        verify(orderMapper).entityToDTO(order);
    }

    @Test
    public void updateOrderTest() throws Exception {
        when(userService.getCurrentUser()).thenReturn(user);
        when(orderService.findById(1)).thenReturn(order);
        when(orderMapper.updateEntityFromDTO(orderDTO, order)).thenReturn(order);
        when(orderService.save(order)).thenReturn(order);
        when(orderMapper.entityToDTO(order)).thenReturn(orderDTO);

        ResponseEntity<ApiResponse<OrderDTO>> response = orderController.updateOrder(orderDTO);

        assertEquals(orderDTO, response.getBody().getData());

        verify(userService).getCurrentUser();
        verify(orderService).findById(1);
        verify(orderMapper).updateEntityFromDTO(orderDTO, order);
        verify(orderService).save(order);
        verify(orderMapper).entityToDTO(order);
    }

    @Test
    public void updateOrderNotFoundTest() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(1);

        when(orderService.findById(orderDTO.getId())).thenThrow(OrderNotFoundException.class);

        assertThrows(OrderNotFoundException.class, () -> orderController.updateOrder(orderDTO));

        verify(orderService).findById(orderDTO.getId());
    }

    @Test
    public void updateOrderNoPermissionTest() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(1);

        User otherUser = new User();
        otherUser.setId(2);
        otherUser.setUsername("other");

        when(orderService.findById(orderDTO.getId())).thenReturn(order);
        when(userService.getCurrentUser()).thenReturn(otherUser);

        assertThrows(NoPermissionException.class, () -> orderController.updateOrder(orderDTO));

        verify(orderService).findById(orderDTO.getId());
        verify(userService).getCurrentUser();
    }

    @Test
    public void deleteOrderTest() throws Exception {
        when(userService.getCurrentUser()).thenReturn(user);
        when(orderService.findById(1)).thenReturn(order);
        doNothing().when(orderService).deleteById(1);

        ResponseEntity<ApiResponse<String>> response = orderController.deleteOrder(1);

        assertEquals("Delete order id - " + 1, response.getBody().getMessage());

        verify(userService).getCurrentUser();
        verify(orderService).findById(1);
        verify(orderService).deleteById(1);
    }

    @Test
    public void deleteOrderNotFoundTest() {
        int orderId = 1;
        when(orderService.findById(orderId)).thenThrow(OrderNotFoundException.class);

        assertThrows(OrderNotFoundException.class, () -> orderController.deleteOrder(orderId));

        verify(orderService).findById(orderId);
    }

    @Test
    public void deleteOrderNoPermissionTest() {
        int orderId = 1;
        User otherUser = new User();
        otherUser.setId(2);
        otherUser.setUsername("other");

        when(orderService.findById(orderId)).thenReturn(order);
        when(userService.getCurrentUser()).thenReturn(otherUser);

        assertThrows(NoPermissionException.class, () -> orderController.deleteOrder(orderId));

        verify(orderService).findById(orderId);
        verify(userService).getCurrentUser();
    }
}