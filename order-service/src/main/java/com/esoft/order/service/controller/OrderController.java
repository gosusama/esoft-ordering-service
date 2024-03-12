package com.esoft.order.service.controller;

import com.esoft.order.service.dto.OrderDTO;
import com.esoft.order.service.entity.Order;
import com.esoft.common.config.entity.User;
import com.esoft.common.config.response.ApiResponse;
import com.esoft.common.config.service.UserService;
import com.esoft.order.service.mapper.OrderMapper;
import com.esoft.order.service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.NoPermissionException;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
public class OrderController {
    private final Logger logger = Logger.getLogger(getClass().getName());

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

        // Just admin or created user can see the order
        if (!userService.isAdmin()) {
            checkPermissionWithOrder(order);
        }

        ApiResponse<OrderDTO> response = new ApiResponse<>("success",
                orderMapper.entityToDTO(order));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/users/{uid}")
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> getOrdersByUid(
            @PathVariable int uid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws NoPermissionException {
        User currentUser = userService.getCurrentUser();

        // Just admin or created user can see the orders
        if (currentUser.getId() != uid && !userService.isAdmin()) {
            logger.warning("User " + currentUser.getUsername() +
                    " does not have permission to operate on orders of user id - " + uid);
            throw new NoPermissionException("User " + currentUser.getUsername() +
                    " does not have permission to perform the operation.");
        }

        Pageable pageable = PageRequest.of(page, size);
        ApiResponse<Page<OrderDTO>> response = new ApiResponse<>("success",
                orderService.findByUid(uid, pageable).map(orderMapper::entityToDTO));

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

        // Just created user can update the order
        checkPermissionWithOrder(order);

        order = orderMapper.updateEntityFromDTO(orderDTO, order);

        ApiResponse<OrderDTO> response = new ApiResponse<>("success",
                orderMapper.entityToDTO(orderService.save(order)));

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("orders/{id}")
    public ResponseEntity<ApiResponse<String>> deleteOrder(@PathVariable int id) throws NoPermissionException {
        Order tempOrder = orderService.findById(id);

        // Just created user can delete the order
        checkPermissionWithOrder(tempOrder);

        orderService.deleteById(id);

        ApiResponse<String> response = new ApiResponse<>("success", "Delete order id - " + id);

        return ResponseEntity.ok(response);
    }

    public void checkPermissionWithOrder(Order order) throws NoPermissionException {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getId() != order.getCreateUser().getId()) {
            logger.warning("User " + currentUser.getUsername() +
                    " does not have permission to operate on order id - " + order.getId());
            throw new NoPermissionException("User " + currentUser.getUsername() +
                    " does not have permission to perform the operation.");
        }
    }
}
