package com.shopsphere.admin.service;

import com.shopsphere.admin.client.OrderServiceClient;
import com.shopsphere.admin.dto.OrderDto;
import com.shopsphere.admin.dto.ReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{
    private final OrderServiceClient orderServiceClient;

    @Override
    public ReportDto getFullReport() {
        List<OrderDto> orders = orderServiceClient.getAllOrders();

        double totalRevenue = orders.stream()
                .mapToDouble(OrderDto::getTotalAmount).sum();

        long totalOrders = orders.size();

        double avgOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0.0;

        Map<String, Long> ordersByStatus = orders.stream()
                .collect(Collectors.groupingBy(OrderDto::getStatus, Collectors.counting()));

        Map<String, Double> revenueByStatus = orders.stream()
                .collect(Collectors.groupingBy(
                        OrderDto::getStatus,
                        Collectors.summingDouble(OrderDto::getTotalAmount)
                ));

        return ReportDto.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .averageOrderValue(avgOrderValue)
                .ordersByStatus(ordersByStatus)
                .revenueByStatus(revenueByStatus)
                .build();
    }
}
