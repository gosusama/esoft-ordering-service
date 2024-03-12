package com.esoft.order.service.service;

import com.esoft.order.service.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    Order findById(int id);

    Page<Order> findByUid(int uid, Pageable pageable);

    Order save(Order order);

    void deleteById(int id);
}
