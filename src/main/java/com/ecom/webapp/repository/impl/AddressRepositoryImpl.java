package com.ecom.webapp.repository.impl;

import com.ecom.webapp.model.Address;
import com.ecom.webapp.model.User;
import com.ecom.webapp.repository.AddressRepository;
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
public class AddressRepositoryImpl implements AddressRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public List<Address> getAddressesByUser(User user) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Address> criteria = builder.createQuery(Address.class);
        Root<Address> root = criteria.from(Address.class);
        criteria.select(root);
        criteria.where(builder.equal(root.get("user"), user));
        return session.createQuery(criteria).getResultList();
    }

    @Override
    public Address getAddressById(int id) {
        Session session = sessionFactory.getObject().getCurrentSession();
        return session.get(Address.class, id);
    }

    @Override
    public void createAddress(Address address) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.persist(address);
    }

    @Override
    public void updateAddress(Address address) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.merge(address);

    }

    @Override
    public void deleteAddress(Address address) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.remove(address);
    }
}
