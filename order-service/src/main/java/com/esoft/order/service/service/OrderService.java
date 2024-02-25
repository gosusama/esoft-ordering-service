package com.esoft.order.service.service;

import com.esoft.common.config.entity.Order;

import java.util.List;

public interface OrderService {
    Order findById(int id);

    public List<Order> findByUid(int uid);

    Order save(Order order);

    void deleteById(int id);
}
