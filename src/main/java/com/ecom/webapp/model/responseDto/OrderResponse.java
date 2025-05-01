package com.ecom.webapp.model.responseDto;

import com.ecom.webapp.model.Order;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
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
    private StoreResponse2 store;
    private Set<OrderDetailResponse> orderDetails;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Instant dateCreated;

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.userId = order.getUser().getId();
        this.addressId = order.getAddress().getId();
        this.shippingFee = order.getShippingFee();
        this.total = order.getTotal();
        this.deliveryStatus = order.getDeliveryStatus();
        this.paymentMethod = order.getPaymentMethod();
        this.store = new StoreResponse2(order.getStore());
        this.orderDetails = order.getOrderDetails().stream()
                .map(OrderDetailResponse::new).collect(Collectors.toSet());
        this.dateCreated = order.getDateCreated();
    }

}
