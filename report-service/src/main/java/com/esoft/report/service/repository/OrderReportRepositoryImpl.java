package com.esoft.report.service.repository;

import com.esoft.report.service.dto.OrderSummaryReportDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
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
        Query query = entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM orders WHERE create_uid = :uid"
        );
        query.setParameter("uid", uid);

        return (Long) query.getSingleResult();
    }

    @Override
    public BigDecimal sumAmountByUserId(int uid) {
        Query query = entityManager.createNativeQuery(
                "SELECT SUM(amount) FROM orders WHERE create_uid = :uid"
        );
        query.setParameter("uid", uid);

        return (BigDecimal) query.getSingleResult();
    }

    @Override
    public OrderSummaryReportDTO findCountOrdersAndSumAmountByUid(int uid) {
        String sql = "SELECT COUNT(*) as totalOrders, SUM(amount) as totalOrderValue " +
                "FROM orders " +
                "WHERE create_uid = :uid";

        Query query = entityManager.createNativeQuery(sql);

        query.setParameter("uid", uid);

        Object[] result = (Object[]) query.getSingleResult();
        if (result != null) {
            return new OrderSummaryReportDTO(((Number) result[0]).longValue(), (BigDecimal) result[1]);
        } else {
            return new OrderSummaryReportDTO(0L, new BigDecimal(0));
        }
    }

    @Override
    public OrderSummaryReportDTO findCountOrdersAndSumAmount(Integer year, Integer month) {
        String sql = "SELECT COUNT(*) as totalOrders, SUM(amount) as totalOrderValue " +
                "FROM orders " +
                "WHERE YEAR(create_date) = :year";
        if (month != null) {
            sql += " AND MONTH(create_date) = :month";
        }

        Query query = entityManager.createNativeQuery(sql);

        query.setParameter("year", year);
        if (month != null) {
            query.setParameter("month", month);
        }

        Object[] result = (Object[]) query.getSingleResult();
        if (result != null) {
            return new OrderSummaryReportDTO(((Number) result[0]).longValue(), (BigDecimal) result[1]);
        } else {
            return new OrderSummaryReportDTO(0L, new BigDecimal(0));
        }
    }
}
