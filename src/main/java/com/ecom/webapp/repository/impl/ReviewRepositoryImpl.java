package com.ecom.webapp.repository.impl;

import com.ecom.webapp.model.Product;
import com.ecom.webapp.model.Review;
import com.ecom.webapp.model.Store;
import com.ecom.webapp.repository.ReviewRepository;
import jakarta.persistence.Query;
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
    private static final int PAGE_SIZE = 2;
    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public List<Review> getReviews(Store store, Integer productId, int page) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Review> criteria = builder.createQuery(Review.class);
        Root<Review> root = criteria.from(Review.class);
        criteria.select(root);



        List<Predicate> predicates = new ArrayList<Predicate>();
        if (productId != null) {
            Predicate p1 = builder.equal(root.get("product").get("id"), productId);
            predicates.add(p1);
        }
        Predicate p = builder.equal(root.get("store").get("id"), store.getId());
        predicates.add(p);
        criteria.where(predicates.toArray(Predicate[]::new));
        criteria.orderBy(builder.desc(root.get("id")));
        Query query = session.createQuery(criteria);
        query.setFirstResult((page - 1) * PAGE_SIZE); // offset
        query.setMaxResults(PAGE_SIZE);               // limit

        return query.getResultList();
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

    @Override
    public Review getReviewById(int reviewId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        return session.get(Review.class, reviewId);
    }
}
