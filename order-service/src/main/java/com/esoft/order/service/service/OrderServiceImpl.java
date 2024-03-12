package com.esoft.order.service.service;

import com.esoft.common.config.response.OrderNotFoundException;
import com.esoft.order.service.entity.Order;
import com.esoft.order.service.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class OrderServiceImpl implements OrderService {
    private final Logger logger = Logger.getLogger(getClass().getName());

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order findById(int id) {
        Optional<Order> result = orderRepository.findById(id);

        if (result.isPresent()) {
           return result.get();
        } else {
            logger.warning("Order id not found - " + id);
            throw new OrderNotFoundException("Order id not found - " + id);
        }
    }

    @Override
    public Page<Order> findByUid(int uid, Pageable pageable) {
        return orderRepository.findByCreateUserId(uid, pageable);
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
