package com.ecom.webapp.repository;

import com.ecom.webapp.model.Order;
import com.ecom.webapp.model.Store;
import com.ecom.webapp.model.User;

import java.util.List;

public interface OrderRepository {
    List<Order> getOrdersByUser(User user, String status, int page);
    List<Order> getOrdersByStore(Store store, String status, int page);
    Order getOrderById(int id);
    void createOrder(Order order);
    void updateOrder(Order order);
    void deleteOrder(Order order);
}
