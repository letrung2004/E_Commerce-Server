package com.ecom.webapp.model.responseDto;

import com.ecom.webapp.model.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private int id;
    private int userId;
    private int addressId;
    private BigDecimal shippingFee;
    private BigDecimal total;
    private String deliveryStatus;
    private String paymentMethod;
    private Set<OrderDetailResponse> orderDetails;

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.userId = order.getUser().getId();
        this.addressId = order.getAddress().getId();
        this.shippingFee = order.getShippingFee();
        this.total = order.getTotal();
        this.deliveryStatus = order.getDeliveryStatus();
        this.paymentMethod = order.getPaymentMethod();
        this.orderDetails = order.getOrderDetails().stream()
                .map(OrderDetailResponse::new).collect(Collectors.toSet());
    }

}
