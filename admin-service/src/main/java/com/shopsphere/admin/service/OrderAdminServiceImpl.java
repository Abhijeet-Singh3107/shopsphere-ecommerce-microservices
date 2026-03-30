package com.shopsphere.admin.service;

import com.shopsphere.admin.client.OrderServiceClient;
import com.shopsphere.admin.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderAdminServiceImpl implements OrderAdminService{

    private final OrderServiceClient orderServiceClient;

    @Override
    public List<OrderDto> getAllOrders() {
        return orderServiceClient.getAllOrders();
    }

    @Override
    public List<OrderDto> getOrdersByStatus(String status) {
        return orderServiceClient.getOrdersByStatus(status);
    }

    @Override
    public OrderDto getOrderById(Long id) {
        return orderServiceClient.getOrderById(id);
    }

    @Override
    public OrderDto updateOrderStatus(Long orderId, String status) {
        return orderServiceClient.updateOrderStatus(orderId, status);
    }
}
