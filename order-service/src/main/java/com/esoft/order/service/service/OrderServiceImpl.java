package com.esoft.order.service.service;

import com.esoft.common.config.entity.Order;
import com.esoft.common.config.entity.User;
import com.esoft.common.config.service.UserService;
import com.esoft.order.service.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    private final UserService userService;

    public OrderServiceImpl(OrderRepository orderRepository, UserService userService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
    }

    @Override
    public Order findById(int id) {
        Optional<Order> result = orderRepository.findById(id);

        Order order = null;
        if (result.isPresent()) {
            order = result.get();
        }

        return order;
    }

    @Override
    public List<Order> findByUid(int uid) {
        return orderRepository.findByCreateUserId(uid);
    }

    @Override
    public Order save(Order order) {
        if (order.getId() == 0) {
            // Generate a unique code
            String generatedCode = generateUniqueCode(order.getCreateUser().getId());

            // Check if the generated code is already in use
            while (orderRepository.existsByCode(generatedCode)) {
                generatedCode = generateUniqueCode(order.getCreateUser().getId()); // Regenerate the code if it's not unique
            }

            order.setCode(generatedCode);

            order.setCreateDate(LocalDateTime.now());
        } else {
            order.setWriteDate(LocalDateTime.now());
        }

        return orderRepository.save(order);
    }

    @Override
    public void deleteById(int id) {
        orderRepository.deleteById(id);
    }

    public String generateUniqueCode(int uid) {
        return System.currentTimeMillis() + "_" + uid;
    }
}
