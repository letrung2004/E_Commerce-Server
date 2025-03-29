package com.ecom.webapp.repository.impl;

import com.ecom.webapp.model.Order;
import com.ecom.webapp.model.User;
import com.ecom.webapp.repository.OrderRepository;
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
public class OrderRepositoryImpl implements OrderRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public List<Order> getOrdersByUser(User user) {
        Session session = this.sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
        Root<Order> root = criteria.from(Order.class);
        criteria.where(builder.equal(root.get("user"), user));
        return session.createQuery(criteria).getResultList();
    }

    @Override
    public Order getOrderById(int id) {
        Session session = this.sessionFactory.getObject().getCurrentSession();
        return session.get(Order.class, id);
    }

    @Override
    public void createOrder(Order order) {
        Session session = this.sessionFactory.getObject().getCurrentSession();
        session.persist(order);
    }

    @Override
    public void updateOrder(Order order) {
        Session session = this.sessionFactory.getObject().getCurrentSession();
        session.merge(order);
    }

    @Override
    public void deleteOrder(Order order) {
        Session session = this.sessionFactory.getObject().getCurrentSession();
        session.remove(order);
    }
}
