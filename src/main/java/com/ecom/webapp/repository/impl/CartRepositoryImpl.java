package com.ecom.webapp.repository.impl;

import com.ecom.webapp.model.Cart;
import com.ecom.webapp.model.SubCart;
import com.ecom.webapp.model.SubCartItem;
import com.ecom.webapp.model.User;
import com.ecom.webapp.repository.CartRepository;
import com.ecom.webapp.repository.UserRepository;
import jakarta.persistence.NoResultException;
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
public class CartRepositoryImpl implements CartRepository {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LocalSessionFactoryBean sessionFactory;


    @Override
    public Cart getCartByUserId(int userId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Cart> criteria = builder.createQuery(Cart.class);
        Root<Cart> root = criteria.from(Cart.class);

        criteria.select(root).where(builder.equal(root.get("user").get("id"), userId));
        try {
            return session.createQuery(criteria).uniqueResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Cart createNewCart(int userId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        Cart cart = new Cart();
        User user = userRepository.getById(userId);
        cart.setUser(user);
        cart.setItemsNumber(0);
        session.persist(cart);
        return cart;
    }

    @Override
    public void updateCart(Cart cart) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.merge(cart);
    }

    @Override
    public void deleteCart(Cart cart) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.remove(cart);
    }
}
