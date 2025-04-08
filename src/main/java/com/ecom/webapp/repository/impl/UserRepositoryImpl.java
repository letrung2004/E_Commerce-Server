package com.ecom.webapp.repository.impl;

import com.cloudinary.Cloudinary;
import com.ecom.webapp.model.User;
import com.ecom.webapp.repository.UserRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {
    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Autowired
    private SessionFactory sf;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<User> getUsers() {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> criteria = builder.createQuery(User.class);
        Root<User> root = criteria.from(User.class);
        criteria.select(root);
        return session.createQuery(criteria).getResultList();
    }

    @Override
    public User getById(Integer id) {
        Session session = sessionFactory.getObject().getCurrentSession();
        return session.get(User.class, id);
    }

    @Override
    public void save(User user) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.persist(user);
    }

    @Override
    public void update(User user) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.merge(user);
    }

    @Override
    public void delete(User user) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.remove(session.get(User.class, user.getId()));
    }

    @Override
    public boolean authenticate(String username, String password) {
        User user = getUserByUsername(username);
        return this.passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public User getUserByUsername(String username) {
        try (Session session = this.sf.openSession()) {
            Query query = session.createQuery("from User where username=:u");
            query.setParameter("u", username);
            return (User) query.getSingleResult();
        }
    }


}

