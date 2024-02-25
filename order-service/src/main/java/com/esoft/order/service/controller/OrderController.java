package com.esoft.order.service.controller;

import com.esoft.common.config.dto.OrderDTO;
import com.esoft.common.config.entity.Order;
import com.esoft.common.config.entity.User;
import com.esoft.common.config.response.ApiResponse;
import com.esoft.common.config.mapper.OrderMapper;
import com.esoft.common.config.service.UserService;
import com.esoft.order.service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.NoPermissionException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {
    private final OrderService orderService;

    private final UserService userService;

    private final OrderMapper orderMapper;

    @Autowired
    public OrderController(OrderService orderService, UserService userService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.userService = userService;
        this.orderMapper = orderMapper;
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrder(@PathVariable int id) throws NoPermissionException {
        Order order = orderService.findById(id);

        if (order == null) {
            throw new RuntimeException("Order id not found - " + id);
        }

        if (!userService.isAdmin()) {
            checkPermissionWithOrder(order);
        }

        ApiResponse<OrderDTO> response = new ApiResponse<>("success",
                orderMapper.entityToDTO(orderService.save(order)));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/users/{uid}")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getOrdersByUid(@PathVariable int uid) throws NoPermissionException {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getId() != uid && !userService.isAdmin()) {
            throw new NoPermissionException("User " + currentUser.getUsername() +
                    " does not have permission to perform the operation.");
        }

        ApiResponse<List<OrderDTO>> response = new ApiResponse<>("success",
                orderMapper.entitiesToDTOs(orderService.findByUid(uid)));


        return ResponseEntity.ok(response);
    }

    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<OrderDTO>> addOrder(@Valid @RequestBody OrderDTO orderDTO) {
        // also just in case they pass an id in JSON ... set id to 0
        // this is to force a save of new item ... instead of update
        orderDTO.setId(0);

        Order order = orderMapper.dtoToEntity(orderDTO);
        order.setCreateUser(userService.getCurrentUser());

        ApiResponse<OrderDTO> response = new ApiResponse<>("success",
                orderMapper.entityToDTO(orderService.save(order)));

        return ResponseEntity.ok(response);
    }

    @PutMapping("orders")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrder(@Valid @RequestBody OrderDTO orderDTO) throws NoPermissionException {
        Order order = orderService.findById(orderDTO.getId());

        checkPermissionWithOrder(order);

        order = orderMapper.updateEntityFromDTO(orderDTO, order);

        ApiResponse<OrderDTO> response = new ApiResponse<>("success",
                orderMapper.entityToDTO(orderService.save(order)));

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("orders/{id}")
    public ResponseEntity<ApiResponse<String>> deleteEmployee(@PathVariable int id) throws NoPermissionException {
        Order tempOrder = orderService.findById(id);

        // throw exception if null
        if (tempOrder == null) {
            throw new RuntimeException("Order id not found - " + id);
        }

        checkPermissionWithOrder(tempOrder);

        orderService.deleteById(id);

        ApiResponse<String> response = new ApiResponse<>("success", "Delete order id - " + id);

        return ResponseEntity.ok(response);
    }

    public void checkPermissionWithOrder(Order order) throws NoPermissionException {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getId() != order.getCreateUser().getId()) {
            throw new NoPermissionException("User " + currentUser.getUsername() +
                    " does not have permission to perform the operation.");
        }
    }
}
