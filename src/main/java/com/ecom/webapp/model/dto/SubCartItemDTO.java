package com.ecom.webapp.model.dto;

import com.ecom.webapp.model.SubCartItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class SubCartItemDTO {
    private int id;
    private ProductDTO product;
    private int quantity;
    private BigDecimal unitPrice;

    public SubCartItemDTO(SubCartItem item) {
        this.id = item.getId();
        this.product = new ProductDTO(item.getProduct());
        this.quantity = item.getQuantity();
        this.unitPrice = item.getUnitPrice();
    }
}

