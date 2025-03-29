package com.ecom.webapp.repository.impl;

import com.ecom.webapp.model.SubCart;
import com.ecom.webapp.repository.SubCartRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SubCartRepositoryImpl implements SubCartRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public void deleteSubCart(SubCart subCart) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.remove(subCart);
    }
}
