package com.ecom.webapp.repository.impl;

import com.ecom.webapp.model.SubCart;
import com.ecom.webapp.model.SubCartItem;
import com.ecom.webapp.repository.SubCartRepository;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import com.ecom.webapp.repository.SubCartRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class SubCartRepositoryImpl implements SubCartRepository {
    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public List<SubCart> getSubCartsByCartId(int cartId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<SubCart> criteria = builder.createQuery(SubCart.class);
        Root<SubCart> root = criteria.from(SubCart.class);

        criteria.select(root).where(builder.equal(root.get("cart").get("id"), cartId));
        return session.createQuery(criteria).getResultList();
    }

    @Override
    public SubCart getByCartIdAndStoreId(int cartId, int storeId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<SubCart> criteria = builder.createQuery(SubCart.class);
        Root<SubCart> root = criteria.from(SubCart.class);
//        System.out.println("getByCartIdAndStoreId - cart: " + cartId );
//        System.out.println("getByCartIdAndStoreId - store: " + storeId );
        criteria.select(root)
                .where(builder.equal(root.get("cart").get("id"), cartId),
                        builder.equal(root.get("store").get("id"), storeId));
        try {
//            System.out.println("getByCartIdAndStoreId - subcart: " + session.createQuery(criteria).getSingleResult().getId() );
            return session.createQuery(criteria).getSingleResult();
        } catch (NoResultException e) {
            System.out.println("DEBUG - Không tìm thấy SubCart cho cartId: " + cartId + " và storeId: " + storeId);
            return null;
        }
    }

    @Override
    public void save(SubCart subCart) {
        Session session = sessionFactory.getObject().getCurrentSession();
        if (subCart.getId() != null) {
            session.merge(subCart);
        }
        else {
            session.persist(subCart);
        }
    }

//     @Override
//     public void delete(SubCart subCart) {
//         Session session = sessionFactory.getObject().getCurrentSession();
//         session.remove(subCart);
//     }

    @Override
    public void deleteSubCart(SubCart subCart) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.remove(subCart);
    }
}
