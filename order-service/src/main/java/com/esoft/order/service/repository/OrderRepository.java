package com.esoft.order.service.repository;

import com.esoft.common.config.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByCreateUserId(int uid);

    boolean existsByCode(String code);
}
