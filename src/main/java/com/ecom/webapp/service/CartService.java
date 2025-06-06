package com.ecom.webapp.service;


import com.ecom.webapp.model.dto.CartDTO;

import java.util.List;


public interface CartService {
    CartDTO getCartDetails(int userId);

    CartDTO handelAddProductToCart(int userId, int productId, int quantity);

    void handelRemoveProductFromCart(int userId, int productId);

    boolean updateQuantity(int subCartId, int productId, int quantityChange);

//    CartDTO checkoutCart(int userId, List<Integer> productIds);
}
