package com.ecom.webapp.repository.impl;

import com.ecom.webapp.model.User;
import com.ecom.webapp.repository.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final SessionFactory sessionFactory;

    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User getUserByUsername(String username) {
        try (Session session = this.sessionFactory.openSession()) {
            Query query = session.createQuery("from User where username=:u");
            query.setParameter("u", username);
            return (User) query.getSingleResult();
        }
    }
}
