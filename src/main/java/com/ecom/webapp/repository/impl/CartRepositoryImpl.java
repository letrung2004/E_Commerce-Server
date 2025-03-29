package com.ecom.webapp.repository.impl;

import com.ecom.webapp.model.Cart;
import com.ecom.webapp.repository.CartRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class CartRepositoryImpl implements CartRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public void deleteCart(Cart cart) {
        Session session = this.sessionFactory.getObject().getCurrentSession();
        session.remove(cart);
    }

    @Override
    public void updateCart(Cart cart) {
        Session session = this.sessionFactory.getObject().getCurrentSession();
        session.merge(cart);
    }
}
