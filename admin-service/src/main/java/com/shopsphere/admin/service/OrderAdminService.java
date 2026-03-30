package com.shopsphere.admin.service;

import com.shopsphere.admin.dto.OrderDto;

import java.util.List;

public interface OrderAdminService {

    List<OrderDto> getAllOrders();

    List<OrderDto> getOrdersByStatus(String status);

    OrderDto getOrderById(Long id);

    OrderDto updateOrderStatus(Long orderId, String status);
}
