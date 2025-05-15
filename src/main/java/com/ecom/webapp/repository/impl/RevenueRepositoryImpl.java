package com.ecom.webapp.repository.impl;

import com.ecom.webapp.model.Order;
import com.ecom.webapp.model.OrderDetail;
import com.ecom.webapp.model.Product;
import com.ecom.webapp.model.Store;
import com.ecom.webapp.model.dto.ProductAndCategoryRevenueDTO;
import com.ecom.webapp.model.dto.RevenueDTO;
import com.ecom.webapp.model.dto.StatisticsDTO;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
@Transactional
public class RevenueRepositoryImpl implements RevenueRepository {
    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public List<Tuple> getRevenueDataByPeriod(int storeId, String period) {
        Session session = Objects.requireNonNull(sessionFactory.getObject()).getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Tuple> q = cb.createTupleQuery();
        Root<Order> root = q.from(Order.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("store").get("id"), storeId));
        predicates.add(cb.equal(root.get("deliveryStatus"), "Đã giao"));

        Expression<?> groupByExpr;
        switch (period) {
            case "month":
                groupByExpr = cb.function("DATE_FORMAT", String.class, root.get("dateCreated"), cb.literal("%Y-%m"));
                break;
            case "quarter":
                groupByExpr = cb.concat(
                        cb.function("YEAR", String.class, root.get("dateCreated")),
                        cb.concat("-Q", cb.function("QUARTER", String.class, root.get("dateCreated")))
                );
                break;
            case "year":
                groupByExpr = cb.function("YEAR", String.class, root.get("dateCreated"));
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

        return session.createQuery(q).getResultList();
    }

    @Override
    public List<Tuple> getRevenueByProductRaw(int storeId, String period, String timeValue) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Tuple> q = cb.createTupleQuery();

        Root<OrderDetail> orderDetailRoot  = q.from(OrderDetail.class);
        Join<OrderDetail, Order> orderJoin = orderDetailRoot.join("order");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(orderJoin.get("store").get("id"), storeId));
        predicates.add(cb.equal(orderJoin.get("deliveryStatus"), "Đã giao"));

        addTimePeriodPredicate(cb, q, orderJoin, predicates, period, timeValue);

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

        return session.createQuery(q).getResultList();
    }

    @Override
    public List<Tuple> getRevenueByCategoryRaw(int storeId, String period, String timeValue) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Tuple> q = cb.createTupleQuery();

        Root<OrderDetail> orderDetailRoot  = q.from(OrderDetail.class);
        Join<OrderDetail, Order> orderJoin = orderDetailRoot.join("order");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(orderJoin.get("store").get("id"), storeId));
        predicates.add(cb.equal(orderJoin.get("deliveryStatus"), "Đã giao"));

        addTimePeriodPredicate(cb, q, orderJoin, predicates, period, timeValue);

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

        return session.createQuery(q).getResultList();
    }

    @Override
    public Tuple getOverallStatisticsRaw(String timeRange, String specificTime) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Tuple> q = cb.createTupleQuery();
        Root<OrderDetail> orderDetailRoot  = q.from(OrderDetail.class);
        Join<OrderDetail, Order> orderJoin = orderDetailRoot.join("order");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(orderJoin.get("deliveryStatus"), "Đã giao"));

        addTimeRangePredicate(cb, orderJoin, predicates, timeRange, specificTime);

        Expression<BigDecimal> totalRevenue = cb.sum(orderDetailRoot.get("subTotal"));
        Expression<Long> orderCount = cb.countDistinct(orderJoin.get("id"));
        Expression<Long> productCount = cb.sum(orderDetailRoot.get("quantity").as(Long.class));

        q.multiselect(
                orderCount.alias("totalOrders"),
                totalRevenue.alias("totalRevenue"),
                productCount.alias("totalProductsSold")
        );

        q.where(cb.and(predicates.toArray(new Predicate[0])));
        List<Tuple> results = session.createQuery(q).getResultList();

        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<Tuple> getChartRevenueRaw(String timeRange, String specificTime) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Tuple> q = cb.createTupleQuery();
        Root<Order> root = q.from(Order.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("deliveryStatus"), "Đã giao"));

        Expression<?> groupByExpr;
        int year = Year.now().getValue();

        if (timeRange.equals("year") || timeRange.equals("quarter")) {
            year = Integer.parseInt(specificTime);
        }

        switch (timeRange) {
            case "month":
                int month = Integer.parseInt(specificTime);
                predicates.add(cb.equal(cb.function("YEAR", Integer.class, root.get("dateCreated")), year));
                predicates.add(cb.equal(cb.function("MONTH", Integer.class, root.get("dateCreated")), month));
                groupByExpr = cb.function("DAY", String.class, root.get("dateCreated"));
                break;

            case "quarter":
                int quarter = Integer.parseInt(specificTime);
                int startMonth = (quarter - 1) * 3 + 1;
                int endMonth = startMonth + 2;
                predicates.add(cb.equal(cb.function("YEAR", Integer.class, root.get("dateCreated")), year));
                predicates.add(cb.between(cb.function("MONTH", Integer.class, root.get("dateCreated")), startMonth, endMonth));
                groupByExpr = cb.function("MONTH", String.class, root.get("dateCreated"));
                break;

            case "year":
                predicates.add(cb.equal(cb.function("YEAR", Integer.class, root.get("dateCreated")), year));
                groupByExpr = cb.function("MONTH", String.class, root.get("dateCreated"));
                break;

            default:
                throw new IllegalArgumentException("Invalid timeRange: " + timeRange);
        }

        Expression<BigDecimal> revenue = cb.sum(cb.diff(root.get("total"), root.get("shippingFee")));
        q.multiselect(groupByExpr.alias("period"), revenue.alias("totalRevenue"));
        q.where(cb.and(predicates.toArray(new Predicate[0])));
        q.groupBy(groupByExpr);
        q.orderBy(cb.asc(groupByExpr));

        return session.createQuery(q).getResultList();
    }

    @Override
    public List<Tuple> getTopProductRevenueRaw(String timeRange, String specificTime, int limit) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Tuple> q = cb.createTupleQuery();
        Root<OrderDetail> orderDetailRoot  = q.from(OrderDetail.class);
        Join<OrderDetail, Order> orderJoin = orderDetailRoot.join("order");
        Join<OrderDetail, Product> productJoin = orderDetailRoot.join("product");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(orderJoin.get("deliveryStatus"), "Đã giao"));

        addTimeRangePredicate(cb, orderJoin, predicates, timeRange, specificTime);
        Expression<Long> productCount = cb.sum(orderDetailRoot.get("quantity").as(Long.class));

        q.multiselect(
                productJoin.get("id").alias("productId"),
                productJoin.get("name").alias("productName"),
                productCount.alias("totalProductsSold")

        );
        q.where(cb.and(predicates.toArray(new Predicate[0])));
        q.groupBy(productJoin.get("id"), productJoin.get("name"));
        q.orderBy(cb.desc(productCount));

        return session.createQuery(q).setMaxResults(limit).getResultList();
    }

    @Override
    public List<Tuple> getTopStoreRevenueRaw(String timeRange, String specificTime, int limit) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Tuple> q = cb.createTupleQuery();
        Root<Order> root = q.from(Order.class);
        Join<Order, Store> storeJoin = root.join("store");
        Join<Order, OrderDetail> detailJoin = root.join("orderDetails", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("deliveryStatus"), "Đã giao"));
        addTimeRangePredicate(cb, root, predicates, timeRange, specificTime);

        Expression<BigDecimal> totalRevenue = cb.sum(detailJoin.get("subTotal"));


        q.multiselect(
                storeJoin.get("id").alias("storeId"),
                storeJoin.get("name").alias("storeName"),
                totalRevenue.alias("totalRevenue")
        );

        q.where(cb.and(predicates.toArray(new Predicate[0])));
        q.groupBy(storeJoin.get("id"), storeJoin.get("name"));
        q.orderBy(cb.desc(totalRevenue));

        return session.createQuery(q).setMaxResults(limit).getResultList();
    }


    @Override
    public List<Tuple> getStorePerformanceRaw(String timeRange, String specificTime) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Tuple> q = cb.createTupleQuery();

        Root<Order> root = q.from(Order.class);
        Join<Order, Store> storeJoin = root.join("store");
        Join<Order, OrderDetail> detailJoin = root.join("orderDetails", JoinType.LEFT);
        Join<OrderDetail, Product> productJoin = detailJoin.join("product");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("deliveryStatus"), "Đã giao"));
        addTimeRangePredicate(cb, root, predicates, timeRange, specificTime);

        Expression<BigDecimal> totalRevenue = cb.sum(detailJoin.get("subTotal"));
        Expression<Long> orderCount = cb.countDistinct(root.get("id"));
        Expression<Long> totalProductsSold = cb.sum(detailJoin.get("quantity").as(Long.class));
        Expression<Long> productCount = cb.countDistinct(productJoin.get("id"));
        q.multiselect(
                storeJoin.get("id").alias("storeId"),
                storeJoin.get("name").alias("storeName"),
                productCount.alias("productCount"),
                orderCount.alias("orderCount"),
                totalProductsSold.alias("productsSold"),
                totalRevenue.alias("totalRevenue")
        );

        q.where(cb.and(predicates.toArray(new Predicate[0])));
        q.groupBy(storeJoin.get("id"), storeJoin.get("name"));

        return session.createQuery(q).getResultList();
    }


    private void addTimePeriodPredicate(CriteriaBuilder cb, CriteriaQuery<Tuple> q,
                                        Path<Order> orderPath, List<Predicate> predicates,
                                        String period, String timeValue) {
        switch (period) {
            case "month":
                predicates.add(cb.equal(
                        cb.function("DATE_FORMAT", String.class, orderPath.get("dateCreated"), cb.literal("%Y-%m")),
                        timeValue
                ));
                break;
            case "quarter":
                String[] parts = timeValue.split("-Q");
                if (parts.length == 2) {
                    String year = parts[0];
                    int quarter = Integer.parseInt(parts[1]);
                    predicates.add(cb.equal(cb.function("YEAR", String.class, orderPath.get("dateCreated")), year));
                    predicates.add(cb.equal(cb.function("QUARTER", String.class, orderPath.get("dateCreated")), quarter));
                }
                break;
            case "year":
                predicates.add(cb.equal(cb.function("YEAR", String.class, orderPath.get("dateCreated")), timeValue));
                break;
        }
    }


    private void addTimeRangePredicate(CriteriaBuilder cb, Path<Order> orderPath,
                                       List<Predicate> predicates,
                                       String timeRange, String specificTime) {
        int currentYear = Year.now().getValue();
        switch (timeRange) {
            case "month":
                int month = Integer.parseInt(specificTime);
                predicates.add(cb.equal(cb.function("YEAR", Integer.class, orderPath.get("dateCreated")), currentYear));
                predicates.add(cb.equal(cb.function("MONTH", Integer.class, orderPath.get("dateCreated")), month));
                break;

            case "quarter":
                int quarter = Integer.parseInt(specificTime);
                predicates.add(cb.equal(cb.function("YEAR", Integer.class, orderPath.get("dateCreated")), currentYear));
                predicates.add(cb.equal(cb.function("QUARTER", Integer.class, orderPath.get("dateCreated")), quarter));
                break;

            case "year":
                int year = Integer.parseInt(specificTime);
                predicates.add(cb.equal(cb.function("YEAR", Integer.class, orderPath.get("dateCreated")), year));
                break;
        }
    }


}
