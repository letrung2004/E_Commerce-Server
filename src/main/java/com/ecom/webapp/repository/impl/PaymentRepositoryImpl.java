package com.ecom.webapp.repository.impl;

import com.ecom.webapp.model.Payment;
import com.ecom.webapp.repository.PaymentRepository;
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
public class PaymentRepositoryImpl implements PaymentRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public Payment getPaymentById(int id) {
        Session session = sessionFactory.getObject().getCurrentSession();
        return session.get(Payment.class, id);
    }

    @Override
    public List<Payment> findPaymentsByTransactionId(String transactionId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Payment> criteria = builder.createQuery(Payment.class);
        Root<Payment> payment = criteria.from(Payment.class);
        criteria.select(payment);
        criteria.where(builder.equal(payment.get("transactionId"), transactionId));
        return session.createQuery(criteria).getResultList();
    }

    @Override
    public void createPayment(Payment payment) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.persist(payment);
    }

    @Override
    public void updatePayment(Payment payment) {
        Session session = sessionFactory.getObject().getCurrentSession();

    }
}
