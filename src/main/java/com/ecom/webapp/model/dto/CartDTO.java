package com.ecom.webapp.model.dto;

import com.ecom.webapp.model.Cart;
import com.ecom.webapp.model.SubCart;
import com.ecom.webapp.model.SubCartItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CartDTO {
    private int cartId;
    private int userId;
    private int itemsNumber;
    private List<SubCartDTO> subCarts;

    public CartDTO(Cart cart, List<SubCart> subCarts, List<SubCartItem> subCartItems) {
        this.cartId = cart.getId();
        this.userId = cart.getUser().getId();
        this.itemsNumber = cart.getItemsNumber();
        this.subCarts = subCarts.stream()
                .map(subCart -> new SubCartDTO(
                        subCart,
                        subCartItems.stream()
                                .filter(item -> item.getSubCart().getId().equals(subCart.getId()))
                                .map(SubCartItemDTO::new)
                                .toList()))
                .toList();
    }
}

