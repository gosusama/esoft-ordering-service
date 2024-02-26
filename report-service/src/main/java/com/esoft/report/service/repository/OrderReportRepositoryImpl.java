package com.esoft.report.service.repository;

import com.esoft.report.service.dto.OrderSummaryReportDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public class OrderReportRepositoryImpl implements OrderReportRepository {
    private final EntityManager entityManager;

    public OrderReportRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Long countByCreateUserId(int uid) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(*) FROM Order WHERE createUser.id = :uid", Long.class
        );
        query.setParameter("uid", uid);

        return query.getSingleResult();
    }

    @Override
    public BigDecimal sumAmountByUserId(int uid) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
                "SELECT SUM(amount) FROM Order WHERE createUser.id = :uid", BigDecimal.class
        );
        query.setParameter("uid", uid);

        return query.getSingleResult();
    }

    @Override
    public OrderSummaryReportDTO findCountOrdersAndSumAmountByUid(int uid) {
        String sql = "SELECT COUNT(*) as totalOrders, SUM(amount) as totalOrderValue " +
                "FROM Order " +
                "WHERE createUser.id = :uid";

        TypedQuery<OrderSummaryReportDTO> query = entityManager.createQuery(sql, OrderSummaryReportDTO.class);

        query.setParameter("uid", uid);

        return query.getSingleResult();
    }

    @Override
    public OrderSummaryReportDTO findCountOrdersAndSumAmount(Integer year, Integer month) {
        String sql = "SELECT COUNT(*) as totalOrders, SUM(amount) as totalOrderValue " +
                "FROM Order " +
                "WHERE YEAR(createDate) = :year";
        if (month != null) {
            sql += " AND MONTH(createDate) = :month";
        }

        TypedQuery<OrderSummaryReportDTO> query = entityManager.createQuery(sql, OrderSummaryReportDTO.class);

        query.setParameter("year", year);
        if (month != null) {
            query.setParameter("month", month);
        }

        return query.getSingleResult();
    }
}
