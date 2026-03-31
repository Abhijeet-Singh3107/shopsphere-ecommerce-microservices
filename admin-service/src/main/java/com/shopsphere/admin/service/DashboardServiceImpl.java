package com.shopsphere.admin.service;

import com.shopsphere.admin.client.CatalogServiceClient;
import com.shopsphere.admin.client.OrderServiceClient;
import com.shopsphere.admin.dto.DashboardStatsDto;
import com.shopsphere.admin.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService{
    private final OrderServiceClient orderServiceClient;
    private final CatalogServiceClient catalogServiceClient;

    @Override
    public DashboardStatsDto getStats() {
        List<OrderDto> orders = orderServiceClient.getAllOrders();
        long totalProducts = catalogServiceClient.getTotalProductCount();

        long totalOrders     = orders.size();
        double totalRevenue  = orders.stream().mapToDouble(OrderDto::getTotalAmount).sum();
        long paidOrders      = countByStatus(orders, "PAID");
        long pendingOrders   = countByStatus(orders, "DRAFT");
        long shippedOrders   = countByStatus(orders, "SHIPPED");
        long deliveredOrders = countByStatus(orders, "DELIVERED");
        long cancelledOrders = countByStatus(orders, "CANCELLED");

        return DashboardStatsDto.builder()
                .totalOrders(totalOrders)
                .totalProducts(totalProducts)
                .totalRevenue(totalRevenue)
                .paidOrders(paidOrders)
                .pendingOrders(pendingOrders)
                .shippedOrders(shippedOrders)
                .deliveredOrders(deliveredOrders)
                .cancelledOrders(cancelledOrders)
                .build();
    }

    private long countByStatus(List<OrderDto> orders, String status) {
        return orders.stream()
                .filter(o -> status.equalsIgnoreCase(o.getStatus()))
                .count();
    }
}
