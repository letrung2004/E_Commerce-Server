package com.ecom.webapp.repository.impl;

import com.ecom.webapp.model.Product;
import com.ecom.webapp.model.Review;
import com.ecom.webapp.model.Store;
import com.ecom.webapp.repository.ReviewRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
public class ReviewRepositoryImpl implements ReviewRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public List<Review> getReviews(Store store, String productId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Review> criteria = builder.createQuery(Review.class);
        Root<Review> root = criteria.from(Review.class);
        criteria.select(root);


        return List.of();
    }

    @Override
    public void addReview(Review review) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.persist(review);
    }

    @Override
    public void updateReview(Review review) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.merge(review);
    }

    @Override
    public void deleteReview(Review review) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.remove(review);
    }
}
