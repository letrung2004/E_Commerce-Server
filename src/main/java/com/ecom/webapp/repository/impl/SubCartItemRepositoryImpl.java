package com.ecom.webapp.repository.impl;

import com.ecom.webapp.model.SubCartItem;
import com.ecom.webapp.repository.SubCartItemRepository;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class SubCartItemRepositoryImpl implements SubCartItemRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public SubCartItem getSubCartItemById(int subCartItemId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        return session.get(SubCartItem.class, subCartItemId);
    }

    @Override
    public List<SubCartItem> getSubCartItemsBySubCartId(List<Integer> subCartIds) {
        if (subCartIds.isEmpty())
            return new ArrayList<>();

        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<SubCartItem> criteria = builder.createQuery(SubCartItem.class);
        Root<SubCartItem> root = criteria.from(SubCartItem.class);

        criteria.select(root).where(root.get("subCart").get("id").in(subCartIds));

        return session.createQuery(criteria).getResultList();
    }

    @Override
    public void save(SubCartItem subCartItem) {
        Session session = sessionFactory.getObject().getCurrentSession();
        if (subCartItem.getId() !=null){
            session.merge(subCartItem);
        }else {
            session.persist(subCartItem);
        }
    }

    @Override
    public SubCartItem getBySubCartIdAndProductId(int subCartId, int productId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<SubCartItem> criteria = builder.createQuery(SubCartItem.class);
        Root<SubCartItem> root = criteria.from(SubCartItem.class);
        System.out.println("subCartId: " + subCartId + ", productId: " + productId);
        root.fetch("product", JoinType.INNER);
        criteria.select(root)
                .where(builder.equal(root.get("subCart").get("id"), subCartId),
                        builder.equal(root.get("product").get("id"), productId));

        try {
            System.out.println("getByCartIdAndStoreId - subCartItem: " + session.createQuery(criteria).getSingleResult().getId() );
            return session.createQuery(criteria).getSingleResult();
        } catch (NoResultException e) {
            System.out.println("DEBUG - Khong tim thay SubCartItem ");
            return null;
        }
    }

    @Override
    public void delete(SubCartItem subCartItem) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.remove(subCartItem);
    }

    @Override
    public int countBySubCartId(int subCartId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);

        Root<SubCartItem> root = criteria.from(SubCartItem.class);
        criteria.select(builder.count(root))
                .where(builder.equal(root.get("subCart").get("id"), subCartId));

        return session.createQuery(criteria).getSingleResult().intValue();
    }
}
