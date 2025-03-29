package com.ecom.webapp.repository.impl;

import com.ecom.webapp.model.SubCartItem;
import com.ecom.webapp.repository.SubCartItemRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SubCartItemRepositoryImpl implements SubCartItemRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public SubCartItem getSubCartItemById(int subCartItemId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        return session.get(SubCartItem.class, subCartItemId);
    }
}
