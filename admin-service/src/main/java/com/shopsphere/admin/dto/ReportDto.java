package com.shopsphere.admin.dto;

import java.util.Map;
import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ReportDto {
    private double totalRevenue;
    private long totalOrders;
    private double averageOrderValue;
    private Map<String, Long> ordersByStatus;
    private Map<String, Double> revenueByStatus;
}
