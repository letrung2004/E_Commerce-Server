package com.ecom.webapp.service;

import com.ecom.webapp.model.Order;
import com.ecom.webapp.model.dto.OrderDto;
import com.ecom.webapp.model.dto.OrderUpdateDto;
import com.ecom.webapp.model.responseDto.OrderResponse;

import java.util.List;

public interface OrderService {
    List<OrderResponse> getOrdersByUsername(String username, String status, int page);
    List<OrderResponse> getOrdersByStoreId(int storeId, String status, int page);
    OrderResponse getOrdersById(int id);
    void createOrder(OrderDto order);
    void updateOrder(OrderUpdateDto order);
    void deleteOrder(int id);

}
