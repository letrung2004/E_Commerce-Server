package com.ecom.webapp.service;


import com.ecom.webapp.model.dto.CartDTO;


public interface CartService {
    CartDTO getCartDetails(int userId);
    CartDTO handelAddProductToCart(int userId, int productId, int quantity);
    void handelRemoveProductFromCart(int userId, int productId);
    boolean updateQuantity(int subCartId, int productId, int quantityChange);
}
