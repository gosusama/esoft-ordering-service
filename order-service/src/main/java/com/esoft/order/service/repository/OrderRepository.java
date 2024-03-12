package com.esoft.order.service.repository;

import com.esoft.order.service.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    Page<Order> findByCreateUserId(int uid, Pageable pageable);

    boolean existsByCode(String code);
}
