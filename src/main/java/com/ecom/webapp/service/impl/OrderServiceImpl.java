package com.ecom.webapp.service.impl;

import com.ecom.webapp.model.*;
import com.ecom.webapp.model.dto.OrderDto;
import com.ecom.webapp.model.dto.OrderItemDto;
import com.ecom.webapp.model.dto.OrderUpdateDto;
import com.ecom.webapp.model.responseDto.OrderResponse;
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
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private SubCartRepository subCartRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private StoreRepository storeRepository;


    @Override
    @Transactional
    public List<OrderResponse> getOrdersByUsername(String username, String status) {
        User user = this.userRepository.getUserByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("User not found with username: " + username);
        }
        List<Order> orders = this.orderRepository.getOrdersByUser(user, status);
        return orders.stream().map(OrderResponse::new).toList();
    }

    @Override
    public OrderResponse getOrdersById(int id) {
        Order order = this.orderRepository.getOrderById(id);
        if (order == null) {
            throw new EntityNotFoundException("Order not found with id: " + id);
        }
        return new OrderResponse(order);
    }

    @Override
    @Transactional
    public void createOrder(OrderDto orderDto) {
        User user = this.userRepository.getUserByUsername(orderDto.getUsername());
        if (user == null) throw new EntityNotFoundException("User not found with username: " + orderDto.getUsername());
        Address address = this.addressRepository.getAddressById(orderDto.getAddressId());
        if (address == null) throw new EntityNotFoundException("Address not found with id: " + orderDto.getAddressId());
        for (OrderItemDto orderItemDto : orderDto.getSubOrderItems()) {
            Store store = this.storeRepository.getStoreById(orderItemDto.getStoreId());
            if (store == null) throw new EntityNotFoundException("Store not found with id: " + orderItemDto.getStoreId());

            Order order = new Order();
            order.setTotal(BigDecimal.ZERO);
            BigDecimal total = BigDecimal.valueOf(orderItemDto.getShippingCost());

            order.setUser(user);
            order.setAddress(address);
            order.setShippingFee(new BigDecimal(orderItemDto.getShippingCost()));
            order.setDeliveryStatus("Chờ xác nhận");
            order.setPaymentMethod(orderDto.getPaymentMethod());
            order.setStore(store);
            this.orderRepository.createOrder(order);


            for (Integer subCartItemId : orderItemDto.getSubCartItemIds()) {
                SubCartItem subCartItem = this.subCartItemRepository.getSubCartItemById(subCartItemId);
                if (subCartItem == null) {
                    throw new EntityNotFoundException("SubCartItem not found with id: " + subCartItemId);
                }

                SubCart subCart = subCartItem.getSubCart();
                Cart cart = subCart.getCart();

                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);
                orderDetail.setProduct(subCartItem.getProduct());
                orderDetail.setQuantity(subCartItem.getQuantity());
                orderDetail.setSubTotal(subCartItem.getUnitPrice());
                total = total.add(BigDecimal.valueOf(subCartItem.getQuantity()).multiply(subCartItem.getProduct().getPrice()));

                this.orderDetailRepository.createOrderDetail(orderDetail);
                this.subCartItemRepository.deleteSubCartItem(subCartItem);

                subCart.getSubCartItems().remove(subCartItem);

                // Neu subCart khong con item nao thi xoa subCart
                if (subCart.getSubCartItems().isEmpty()) {
                    System.out.println("subCartItems is empty");
                    this.subCartRepository.deleteSubCart(subCart);
                    cart.getSubCarts().remove(subCart);
                    System.out.println("subCartItems is deleted");

                }

                if (cart.getItemsNumber() != 0) {
                    System.out.println("items number before decrease 1: " + cart.getItemsNumber());
                    cart.setItemsNumber(cart.getItemsNumber() - 1);
                    System.out.println("items number after decrease 1: " + cart.getItemsNumber());
                }
                if (cart.getItemsNumber() == 0) {
                    System.out.println("subCarts is empty");
                    this.cartRepository.deleteCart(cart);
                    System.out.println("HERE");

                } else {
                    this.cartRepository.updateCart(cart);
                }

            }

            order.setTotal(total);

            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setUser(user);
            payment.setAmount(order.getTotal());
            switch (orderDto.getPaymentMethod()) {
                case "COD":
                    payment.setPaymentMethod("Cash on delivery");
                    payment.setStatus("Chưa thanh toán");

                    break;
                case "VNPay":
                    payment.setPaymentMethod("VNPay");
                    payment.setStatus("Chờ thanh toán");
                    break;
                default:
                    throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ: " + orderDto.getPaymentMethod());
            }

            this.orderRepository.updateOrder(order);

            this.paymentRepository.createPayment(payment);

        }


    }


//    @Override
//    @Transactional
//    public void createOrder(OrderDto orderDto) {
//        Order order = new Order();
//        order.setTotal(BigDecimal.ZERO);
//
//
//        User user = this.userRepository.getUserByUsername(orderDto.getUsername());
//        if (user == null) throw new EntityNotFoundException("User not found with username: " + orderDto.getUsername());
//        Address address = this.addressRepository.getAddressById(orderDto.getAddressId());
//        if (address == null) throw new EntityNotFoundException("Address not found with id: " + orderDto.getAddressId());
//
//        BigDecimal total = BigDecimal.valueOf(orderDto.getShippingFree());
//
//        order.setUser(user);
//        order.setAddress(address);
//        order.setShippingFee(new BigDecimal(orderDto.getShippingFree()));
//        order.setDeliveryStatus("Chờ xác nhận");
//        order.setPaymentMethod(orderDto.getPaymentMethod());
//        this.orderRepository.createOrder(order);
//
//        for (Integer subCartItemId : orderDto.getSubCartItemIds()) {
//            SubCartItem subCartItem = this.subCartItemRepository.getSubCartItemById(subCartItemId);
//            if (subCartItem == null) {
//                throw new EntityNotFoundException("SubCartItem not found with id: " + subCartItemId);
//            }
//
//            SubCart subCart = subCartItem.getSubCart();
//            Cart cart = subCart.getCart();
//
//            OrderDetail orderDetail = new OrderDetail();
//            orderDetail.setOrder(order);
//            orderDetail.setProduct(subCartItem.getProduct());
//            orderDetail.setQuantity(subCartItem.getQuantity());
//            orderDetail.setUnitPrice(subCartItem.getUnitPrice());
//            total = total.add(BigDecimal.valueOf(subCartItem.getQuantity()).multiply(subCartItem.getUnitPrice()));
//
//            this.orderDetailRepository.createOrderDetail(orderDetail);
//            this.subCartItemRepository.deleteSubCartItem(subCartItem);
//
//            subCart.getSubCartItems().remove(subCartItem);
//
//            // Neu subCart khong con item nao thi xoa subCart
//            if (subCart.getSubCartItems().isEmpty()) {
//                System.out.println("subCartItems is empty");
//                this.subCartRepository.deleteSubCart(subCart);
//                cart.getSubCarts().remove(subCart);
//                System.out.println("subCartItems is deleted");
//
//            }
//
//            if (cart.getItemsNumber() != 0) {
//                System.out.println("items number before decrease 1: " + cart.getItemsNumber());
//                cart.setItemsNumber(cart.getItemsNumber() - 1);
//                System.out.println("items number after decrease 1: " + cart.getItemsNumber());
//            }
//            if (cart.getItemsNumber() == 0) {
//                System.out.println("subCarts is empty");
//                this.cartRepository.deleteCart(cart);
//                System.out.println("HERE");
//
//            } else {
//                this.cartRepository.updateCart(cart);
//            }
//
//        }
//
//        order.setTotal(total);
//
//        Payment payment = new Payment();
//        payment.setOrder(order);
//        payment.setUser(user);
//        payment.setAmount(order.getTotal());
//        switch (orderDto.getPaymentMethod()) {
//            case "COD":
//                payment.setPaymentMethod("Cash on delivery");
//                payment.setStatus("Chưa thanh toán");
//
//                break;
//            case "VNPay":
//                payment.setPaymentMethod("VNPay");
//                // Goi API hoac service VNPay ...
//                break;
//            default:
//                throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ: " + orderDto.getPaymentMethod());
//        }
//
//        this.orderRepository.updateOrder(order);
//
//        this.paymentRepository.createPayment(payment);
//
//
//    }

    @Override
    public void updateOrder(OrderUpdateDto orderUpdateDto) {
        Order order = this.orderRepository.getOrderById(orderUpdateDto.getId());
        if (order == null) throw new EntityNotFoundException("Order not found with id: " + orderUpdateDto.getId());
        order.setDeliveryStatus(orderUpdateDto.getStatus());
        this.orderRepository.updateOrder(order);
    }

    @Override
    public void deleteOrder(int id) {
        Order order = this.orderRepository.getOrderById(id);
        if (order == null) throw new EntityNotFoundException("Order not found with id: " + id);
        Payment payment = order.getPayment();
        if (payment == null) throw new EntityNotFoundException("Payment not found");
        payment.setOrder(null);
        this.paymentRepository.updatePayment(payment);
        this.orderRepository.deleteOrder(order);

    }
}
