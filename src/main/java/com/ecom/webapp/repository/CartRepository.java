package com.ecom.webapp.repository;

import com.ecom.webapp.model.Cart;

public interface CartRepository {
    void deleteCart(Cart cart);
    void updateCart(Cart cart);
}
