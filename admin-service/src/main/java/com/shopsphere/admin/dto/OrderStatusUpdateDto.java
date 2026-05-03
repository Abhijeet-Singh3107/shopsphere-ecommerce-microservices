package com.shopsphere.admin.dto;
import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderStatusUpdateDto {
    private String status;
}
