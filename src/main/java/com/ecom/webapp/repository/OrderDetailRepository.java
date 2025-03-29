package com.ecom.webapp.repository;

import com.ecom.webapp.model.Order;
import com.ecom.webapp.model.OrderDetail;

import java.util.List;

public interface OrderDetailRepository {
    List<OrderDetail> getOrderDetailsByOrder(Order order);
    OrderDetail getOrderDetailById(int id);
    void createOrderDetail(OrderDetail orderDetail);
    void updateOrderDetail(OrderDetail orderDetail);
    void deleteOrderDetail(OrderDetail orderDetail);

}
