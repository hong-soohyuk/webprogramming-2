package com.example.orderservice.service;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;

public interface OrderService {
    OrderDto                createOrder(OrderDto orderDetails);
    OrderDto                cancelOrder(String orderId);
    OrderDto                getOrderByOrderId(String orderId);
    Iterable<OrderEntity>   getOrderByUserEmail(String userEmail);
}
