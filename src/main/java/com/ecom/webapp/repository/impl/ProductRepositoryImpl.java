package com.ecom.webapp.repository.impl;

import com.ecom.webapp.model.Product;
import com.ecom.webapp.repository.ProductRepository;
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
public class ProductRepositoryImpl implements ProductRepository {
    private static final int PAGE_SIZE = 20;
    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    private List<Product> getProductsWithFilter(Map<String, String> params, Integer storeId) {
        Session session = this.sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Product> q = b.createQuery(Product.class);
        Root<Product> root = q.from(Product.class);
        q.select(root);

        List<Predicate> predicates = new ArrayList<>();
        if (storeId != null) {
            predicates.add(b.equal(root.get("store").get("id"), storeId));
        }
        if (params != null) {
            String kw = params.get("q");
            if (kw != null && !kw.isEmpty()) {
                predicates.add(b.like(root.get("name"), "%" + kw + "%"));
            }

            String fromPrice = params.get("fromPrice");
            if (fromPrice != null && !fromPrice.isEmpty()) {
                predicates.add(b.greaterThanOrEqualTo(root.get("price"), Double.parseDouble(fromPrice)));
            }

            String toPrice = params.get("toPrice");
            if (toPrice != null && !toPrice.isEmpty()) {
                predicates.add(b.lessThanOrEqualTo(root.get("price"), Double.parseDouble(toPrice)));
            }

            String cateId = params.get("cateId");
            if (cateId != null && !cateId.isEmpty()) {
                predicates.add(b.equal(root.get("cateId"), Integer.parseInt(cateId)));
            }
        }
        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }

        Query query = session.createQuery(q);
        if (params != null) {
            String page = params.get("page");
            if (page != null && !page.isEmpty()) {
                int p = Integer.parseInt(page);
                int start = (p - 1) * PAGE_SIZE;
                query.setFirstResult(start);
                query.setMaxResults(PAGE_SIZE);
            }
        }

        return query.getResultList();
    }


    @Override
    public List<Product> getProducts(Map<String, String> params) {
        return getProductsWithFilter(params, null);
    }

    @Override
    public void addOrUpdate(Product p) {
        Session session = this.sessionFactory.getObject().getCurrentSession();
        if(p.getId()!=null) {
            session.merge(p);
        }
        else {
            session.persist(p);
        }
    }

    @Override
    public Product getProductById(int id) {
        Session session = this.sessionFactory.getObject().getCurrentSession();
        return session.get(Product.class, id);
    }

    @Override
    public void deleteProduct(int id) {
        Session session = this.sessionFactory.getObject().getCurrentSession();
        Product p = getProductById(id);
        session.remove(p);
    }


    @Override
    public List<Product> getProductsByStore(int storeId, Map<String, String> params) {
        return getProductsWithFilter(params, storeId);
    }



}
