package com.ecom.webapp.repository;

import com.ecom.webapp.model.Cart;
import com.ecom.webapp.model.SubCart;
import com.ecom.webapp.model.SubCartItem;

import java.util.List;

public interface CartRepository {
    Cart getCartByUserId(int userId);
    Cart createNewCart(int userId);
    void updateCart(Cart cart);
    void deleteCart(Cart cart);
}
