package com.shopsphere.admin.dto;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class DashboardStatsDto {
    private long totalOrders;
    private long totalProducts;
    private double totalRevenue;
    private long paidOrders;
    private long pendingOrders;    // DRAFT
    private long shippedOrders;
    private long deliveredOrders;
    private long cancelledOrders;
}
