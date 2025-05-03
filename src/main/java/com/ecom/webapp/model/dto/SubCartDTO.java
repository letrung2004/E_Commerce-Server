package com.ecom.webapp.model.dto;

import com.ecom.webapp.model.SubCart;
import com.ecom.webapp.model.SubCartItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SubCartDTO {
    private int subCartId;
    private int storeId;
    private String storeName;
    private String storeAvatar;
    private List<SubCartItemDTO> items;

    public SubCartDTO(SubCart subCart, List<SubCartItemDTO> subCartItems) {
        this.subCartId = subCart.getId();
        this.storeId = subCart.getStore().getId();
        this.storeName = subCart.getStore().getName();
        this.storeAvatar = subCart.getStore().getLogo();
        this.items = subCartItems;
    }
}


