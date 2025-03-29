package com.ecom.webapp.service.impl;

import com.ecom.webapp.model.*;
import com.ecom.webapp.model.dto.OrderDto;
import com.ecom.webapp.repository.*;
import com.ecom.webapp.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private SubCartItemRepository subCartItemRepository;

    @Override
    public List<Order> getOrdersByUsername(String username) {
        User user = this.userRepository.getUserByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("User not found with username: " + username);
        }
        return this.orderRepository.getOrdersByUser(user);
    }

    @Override
    public Order getOrdersById(int id) {
        return this.orderRepository.getOrderById(id);
    }

    @Override
    public void createOrder(OrderDto orderDto) {
        Order order = new Order();
        order.setTotal(BigDecimal.ZERO);

        User user = this.userRepository.getUserByUsername(orderDto.getUsername());
        if (user == null) throw new EntityNotFoundException("User not found with username: " + orderDto.getUsername());
        Address address = this.addressRepository.getAddressById(orderDto.getAddressId());
        if (address == null) throw new EntityNotFoundException("Address not found with id: " + orderDto.getAddressId());

        BigDecimal total = BigDecimal.valueOf(orderDto.getShippingFree());

        order.setUser(user);
        order.setAddress(address);
        order.setShippingFee(new BigDecimal(orderDto.getShippingFree()));
        order.setDeliveryStatus("Chờ xác nhận");

        this.orderRepository.createOrder(order);

        for (Integer subCartItemId : orderDto.getSubCartItemIds()) {
            SubCartItem subCartItem = this.subCartItemRepository.getSubCartItemById(subCartItemId);
            if (subCartItem == null) {
                throw new EntityNotFoundException("SubCartItem not found with id: " + subCartItemId);
            }
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(subCartItem.getProduct());
            orderDetail.setQuantity(subCartItem.getQuantity());
            orderDetail.setUnitPrice(subCartItem.getUnitPrice());
            total = total.add(BigDecimal.valueOf(subCartItem.getQuantity()).multiply(subCartItem.getUnitPrice()));
            this.orderDetailRepository.createOrderDetail(orderDetail);
        }

        order.setTotal(total);
        this.orderRepository.updateOrder(order);

    }

    @Override
    public void updateOrder(Order order) {

    }

    @Override
    public void deleteOrder(Order order) {

    }
}
