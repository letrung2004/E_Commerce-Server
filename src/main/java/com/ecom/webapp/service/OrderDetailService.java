package com.ecom.webapp.service;

import com.ecom.webapp.model.OrderDetail;

import java.util.List;

public interface OrderDetailService {
    List<OrderDetail> getOrderDetailsByOrderId(int id);
    OrderDetail getOrderDetailById(int id);
    void createOrderDetail(OrderDetail orderDetail);
    void updateOrderDetail(OrderDetail orderDetail);
    void deleteOrderDetail(int id);
}
