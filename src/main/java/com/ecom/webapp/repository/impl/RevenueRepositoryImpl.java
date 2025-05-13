package com.ecom.webapp.repository.impl;

import com.ecom.webapp.model.Order;
import com.ecom.webapp.model.OrderDetail;
import com.ecom.webapp.model.dto.ProductAndCategoryRevenueDTO;
import com.ecom.webapp.model.dto.RevenueDTO;
import com.ecom.webapp.repository.RevenueRepository;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
@Transactional
public class RevenueRepositoryImpl implements RevenueRepository {
    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public List<RevenueDTO> getRevenueByPeriod(int storeId, String period) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Tuple> q = cb.createTupleQuery();
        Root<Order> root = q.from(Order.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("store").get("id"), storeId));
        predicates.add(cb.equal(root.get("deliveryStatus"), "Đã giao"));

        Expression<?> groupByExpr;
        List<String> allPeriods;

        Year currentYear = Year.now();
        int year = currentYear.getValue();

        switch (period) {
            case "month":
                groupByExpr = cb.function("DATE_FORMAT", String.class, root.get("dateCreated"), cb.literal("%Y-%m"));
                allPeriods = IntStream.rangeClosed(1, LocalDate.now().getMonthValue())
                        .mapToObj(m -> String.format("%d-%02d", year, m))
                        .collect(Collectors.toList());
                break;

            case "quarter":
                groupByExpr = cb.concat(
                        cb.function("YEAR", String.class, root.get("dateCreated")),
                        cb.concat("-Q", cb.function("QUARTER", String.class, root.get("dateCreated")))
                );
                int currentQuarter = (LocalDate.now().getMonthValue() - 1) / 3 + 1;
                allPeriods = IntStream.rangeClosed(1, currentQuarter)
                        .mapToObj(qtr -> String.format("%d-Q%d", year, qtr))
                        .collect(Collectors.toList());
                break;

            case "year":
                groupByExpr = cb.function("YEAR", String.class, root.get("dateCreated"));
                allPeriods = IntStream.rangeClosed(year - 2, year)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.toList());
                break;

            default:
                throw new IllegalArgumentException("Invalid period: " + period);
        }

        Expression<BigDecimal> totalRevenue = cb.sum(cb.diff(root.get("total"), root.get("shippingFee")));
        Expression<Long> orderCount = cb.count(root.get("id"));
        Expression<Number> average = cb.quot(totalRevenue, cb.toBigDecimal(orderCount));

        q.multiselect(
                groupByExpr.alias("period"),
                totalRevenue.alias("totalRevenue"),
                orderCount.alias("orderCount"),
                average.alias("averagePrice")
        );
        q.where(cb.and(predicates.toArray(new Predicate[0])));
        q.groupBy(groupByExpr);
        q.orderBy(cb.asc(groupByExpr));

        List<Tuple> result = session.createQuery(q).getResultList();
        Map<String, RevenueDTO> resultMap = new HashMap<>();

        for (Tuple r : result) {
            Object periodValue = r.get("period");
            String periodStr = periodValue != null ? periodValue.toString() : "UNKNOWN";

            RevenueDTO dto = new RevenueDTO();
            dto.setPeriod(periodStr);
            dto.setTotalRevenue(r.get("totalRevenue", BigDecimal.class));
            dto.setOrderCount(r.get("orderCount", Long.class));
            dto.setAveragePrice(r.get("averagePrice", BigDecimal.class));

            resultMap.put(periodStr, dto);
        }

        List<RevenueDTO> finalResult = new ArrayList<>();
        for (String p : allPeriods) {
            finalResult.add(resultMap.getOrDefault(p, new RevenueDTO(p, BigDecimal.ZERO, 0L, BigDecimal.ZERO)));
        }

        return finalResult;
    }

    @Override
    public List<ProductAndCategoryRevenueDTO> getRevenueByProduct(int storeId, String period, String timeValue) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Tuple> q = cb.createTupleQuery();

        Root<OrderDetail> orderDetailRoot  = q.from(OrderDetail.class);
        Join<OrderDetail, Order> orderJoin = orderDetailRoot.join("order");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(orderJoin.get("store").get("id"), storeId));
        predicates.add(cb.equal(orderJoin.get("deliveryStatus"), "Đã giao"));

        switch (period) {
            case "month":
                predicates.add(cb.equal(
                        cb.function("DATE_FORMAT", String.class, orderJoin.get("dateCreated"), cb.literal("%Y-%m")),
                        timeValue
                ));
                break;
            case "quarter":
                String[] parts = timeValue.split("-Q");
                if (parts.length == 2) {
                    String year = parts[0];
                    int quarter = Integer.parseInt(parts[1]);
                    predicates.add(cb.equal(cb.function("YEAR", String.class, orderJoin.get("dateCreated")), year));
                    predicates.add(cb.equal(cb.function("QUARTER", String.class, orderJoin.get("dateCreated")), quarter));
                }
                break;
            case "year":
                predicates.add(cb.equal(cb.function("YEAR", String.class, orderJoin.get("dateCreated")), timeValue));
                break;
        }

        Expression<BigDecimal> totalRevenue = cb.sum(orderDetailRoot.get("subTotal"));
        Expression<Long> quantitySold  = cb.sum(orderDetailRoot.get("quantity").as(Long.class));
        Expression<Long> orderCount = cb.countDistinct(orderJoin.get("id"));

        q.multiselect(
                orderDetailRoot.get("product").get("id").alias("productId"),
                orderDetailRoot.get("product").get("name").alias("productName"),
                totalRevenue.alias("totalRevenue"),
                quantitySold.alias("quantitySold"),
                orderCount.alias("orderCount")
        );

        q.where(cb.and(predicates.toArray(new Predicate[0])));
        q.groupBy(
                orderDetailRoot.get("product").get("id"),
                orderDetailRoot.get("product").get("name")
        );
        q.orderBy(cb.desc(totalRevenue));

        List<Tuple> results = session.createQuery(q).getResultList();

        List<ProductAndCategoryRevenueDTO> dtoList = new ArrayList<>();
        for (Tuple r : results) {
            ProductAndCategoryRevenueDTO dto = new ProductAndCategoryRevenueDTO();
            dto.setId(r.get("productId", Integer.class));
            dto.setName(r.get("productName", String.class));
            dto.setPeriod(timeValue);

            BigDecimal revenue = r.get("totalRevenue", BigDecimal.class);
            dto.setTotalRevenue(revenue != null ? revenue : BigDecimal.ZERO);

            Long quantity = r.get("quantitySold", Long.class);
            dto.setQuantitySold(quantity != null ? quantity : 0L);

            Long oCount = r.get("orderCount", Long.class);
            dto.setOrderCount(oCount != null ? oCount : 0L);


            dtoList.add(dto);
        }

        return dtoList;
    }

    @Override
    public List<ProductAndCategoryRevenueDTO> getRevenueByCategory(int storeId, String period, String timeValue) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Tuple> q = cb.createTupleQuery();

        Root<OrderDetail> orderDetailRoot  = q.from(OrderDetail.class);
        Join<OrderDetail, Order> orderJoin = orderDetailRoot.join("order");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(orderJoin.get("store").get("id"), storeId));
        predicates.add(cb.equal(orderJoin.get("deliveryStatus"), "Đã giao"));

        switch (period) {
            case "month":
                predicates.add(cb.equal(
                        cb.function("DATE_FORMAT", String.class, orderJoin.get("dateCreated"), cb.literal("%Y-%m")),
                        timeValue
                ));
                break;
            case "quarter":
                String[] parts = timeValue.split("-Q");
                if (parts.length == 2) {
                    String year = parts[0];
                    int quarter = Integer.parseInt(parts[1]);
                    predicates.add(cb.equal(cb.function("YEAR", String.class, orderJoin.get("dateCreated")), year));
                    predicates.add(cb.equal(cb.function("QUARTER", String.class, orderJoin.get("dateCreated")), quarter));
                }
                break;
            case "year":
                predicates.add(cb.equal(cb.function("YEAR", String.class, orderJoin.get("dateCreated")), timeValue));
                break;
        }

        Expression<BigDecimal> totalRevenue = cb.sum(orderDetailRoot.get("subTotal"));
        Expression<Long> productCount = cb.sum(orderDetailRoot.get("quantity").as(Long.class));
        Expression<Long> orderCount = cb.countDistinct(orderJoin.get("id"));

        q.multiselect(
                orderDetailRoot.get("product").get("category").get("id").alias("categoryId"),
                orderDetailRoot.get("product").get("category").get("name").alias("categoryName"),
                totalRevenue.alias("totalRevenue"),
                productCount.alias("productCount"),
                orderCount.alias("orderCount")
        );

        q.where(cb.and(predicates.toArray(new Predicate[0])));
        q.groupBy(
                orderDetailRoot.get("product").get("category").get("id"),
                orderDetailRoot.get("product").get("category").get("name")
        );
        q.orderBy(cb.desc(totalRevenue));
        List<Tuple> results = session.createQuery(q).getResultList();
        List<ProductAndCategoryRevenueDTO> dtoList  = new ArrayList<>();
        for (Tuple r : results) {
            ProductAndCategoryRevenueDTO dto = new ProductAndCategoryRevenueDTO();
            dto.setId(r.get("categoryId", Integer.class));
            dto.setName(r.get("categoryName", String.class));
            dto.setPeriod(timeValue);

            BigDecimal revenue = r.get("totalRevenue", BigDecimal.class);
            dto.setTotalRevenue(revenue != null ? revenue : BigDecimal.ZERO);

            Long oCount = r.get("orderCount", Long.class);
            dto.setOrderCount(oCount != null ? oCount : 0L); //chỗ này laf số lượng đơn hàng

            Long pCount = r.get("productCount", Long.class);
            dto.setQuantitySold(pCount != null ? pCount : 0L);

            dtoList.add(dto);
        }

        return dtoList;

    }

}
