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
    private ProductDTO product;
    private int quantity;
    private BigDecimal unitPrice;

    public SubCartItemDTO(SubCartItem item) {
        this.product = new ProductDTO(item.getProduct());
        this.quantity = item.getQuantity();
        this.unitPrice = item.getUnitPrice();
    }
}

