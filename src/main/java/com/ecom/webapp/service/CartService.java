package com.ecom.webapp.service;


import com.ecom.webapp.model.dto.CartDTO;


public interface CartService {
    CartDTO getCartDetails(int userId);
    CartDTO handelAddProductToCart(int userId, int productId);
    void handelRemoveProductFromCart(int userId, int productId);
}
