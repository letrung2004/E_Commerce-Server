package com.ecom.webapp.repository.impl;

import com.ecom.webapp.model.Order;
import com.ecom.webapp.model.OrderDetail;
import com.ecom.webapp.repository.OrderDetailRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class OrderDetailRepositoryImpl implements OrderDetailRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public List<OrderDetail> getOrderDetailsByOrder(Order order) {
        Session session = this.sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<OrderDetail> criteria = builder.createQuery(OrderDetail.class);
        Root<OrderDetail> root = criteria.from(OrderDetail.class);
        criteria.select(root).where(builder.equal(root.get("order"), order));
        return session.createQuery(criteria).getResultList();
    }

    @Override
    public OrderDetail getOrderDetailById(int id) {
        Session session = this.sessionFactory.getObject().getCurrentSession();
        return session.get(OrderDetail.class, id);
    }

    @Override
    public void createOrderDetail(OrderDetail orderDetail) {
        Session session = this.sessionFactory.getObject().getCurrentSession();
        session.persist(orderDetail);
    }

    @Override
    public void updateOrderDetail(OrderDetail orderDetail) {
        Session session = this.sessionFactory.getObject().getCurrentSession();
        session.merge(orderDetail);
    }

    @Override
    public void deleteOrderDetail(OrderDetail orderDetail) {
        Session session = this.sessionFactory.getObject().getCurrentSession();
        session.remove(orderDetail);
    }
}
