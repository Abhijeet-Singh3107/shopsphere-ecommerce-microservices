package com.shopsphere.admin.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderDto {
    private Long id;
    private String userEmail;
    private String status;
    private Double totalAmount;
    private String shippingAddress;
    private String paymentReference;
    private List<OrderItemDto> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
