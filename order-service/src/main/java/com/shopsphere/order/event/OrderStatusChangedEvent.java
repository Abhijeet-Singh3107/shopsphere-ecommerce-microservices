package com.shopsphere.order.event;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusChangedEvent {
    private Long orderId;
    private String userEmail;
    private String status;
}