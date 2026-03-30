package com.shopsphere.order.service;

import com.shopsphere.order.dto.CheckoutRequestDto;
import com.shopsphere.order.dto.OrderResponseDto;
import com.shopsphere.order.enums.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderResponseDto checkout(String userEmail, CheckoutRequestDto request);

    List<OrderResponseDto> getMyOrders(String userEmail);

    OrderResponseDto getOrderById(String userEmail, Long orderId);

    OrderResponseDto cancelOrder(String userEmail, Long orderId);

    // Admin-facing — used later by admin-service
    OrderResponseDto updateStatus(Long orderId, OrderStatus status);

    // admin only
    public OrderResponseDto getOrderById(Long id);

    public List<OrderResponseDto> getAllOrders();
}
