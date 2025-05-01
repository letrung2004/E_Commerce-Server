package com.ecom.webapp.model.responseDto;

import com.ecom.webapp.model.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailResponse {
    private int id;
    private ProductResponse product;
    private int quantity;
    private BigDecimal subTotal;
    private boolean isEvaluated;

    public OrderDetailResponse(OrderDetail orderDetail) {
        this.id = orderDetail.getId();
        this.product = new ProductResponse(orderDetail.getProduct());
        this.quantity = orderDetail.getQuantity();
        this.subTotal = orderDetail.getSubTotal();
        this.isEvaluated = orderDetail.isEvaluated();
    }
}
