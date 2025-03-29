package com.ecom.webapp.service;

import com.ecom.webapp.model.Order;
import com.ecom.webapp.model.User;
import com.ecom.webapp.model.dto.OrderDto;

import java.util.List;

public interface OrderService {
    List<Order> getOrdersByUsername(String username);
    Order getOrdersById(int id);
    void createOrder(OrderDto order);
    void updateOrder(Order order);
    void deleteOrder(Order order);

}
