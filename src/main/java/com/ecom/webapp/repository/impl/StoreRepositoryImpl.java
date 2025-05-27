package com.ecom.webapp.repository.impl;

import com.ecom.webapp.model.Store;
import com.ecom.webapp.model.User;
import com.ecom.webapp.repository.StoreRepository;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class StoreRepositoryImpl implements StoreRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public List<Store> getStores(Map<String, String> params) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Store> criteria = builder.createQuery(Store.class);
        Root<Store> storeRoot = criteria.from(Store.class);
        criteria.select(storeRoot);

        List<Predicate> predicates = new ArrayList<>();
        if (params != null) {
            String kw = params.get("q");
            if (kw != null && !kw.isEmpty()) {
                predicates.add(builder.like(storeRoot.get("name"), "%" + kw + "%"));
            }
        }
        if (!predicates.isEmpty()) {
            criteria.where(builder.and(predicates.toArray(new Predicate[0])));
        }

        return session.createQuery(criteria).getResultList();
    }

    @Override
    public List<Object[]> getStoresUnprocessed() {

//        select s.name, u.email, u.full_name, s.description from
//        store s inner join user u
//        on s.owner_id = u.id
//        and u.store_active=false

        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteria = builder.createQuery(Object[].class);
        Root<Store> storeRoot = criteria.from(Store.class);
        Join<Store, User> userJoin = storeRoot.join("owner");

        criteria.multiselect(
                storeRoot.get("name"),
                userJoin.get("email"),
                userJoin.get("username"),
                storeRoot.get("description"),
                userJoin.get("id"),
                storeRoot.get("id")
        );

        criteria.where(builder.equal(userJoin.get("storeActive"), false));

        return session.createQuery(criteria).getResultList();
    }

    @Override
    public Store getStoreByUsername(String username) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Store> criteria = builder.createQuery(Store.class);
        Root<Store> storeRoot = criteria.from(Store.class);
        criteria.where(builder.equal(storeRoot.get("owner").get("username"), username));
        try {
            return session.createQuery(criteria).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public Store getStoreById(int storeId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        return session.get(Store.class, storeId);
    }

    @Override
    public void createStore(Store store) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.persist(store);
    }

    @Override
    public void updateStore(Store store) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.merge(store);
    }

    @Override
    public void deleteStore(Store store) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.remove(store);
    }
}
