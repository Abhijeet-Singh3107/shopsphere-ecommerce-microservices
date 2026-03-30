package com.shopsphere.order.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data @Builder
public class CartResponseDto {
    private Long id;
    private String userEmail;
    private List<CartItemResponseDto> items;
    private BigDecimal totalAmount;
}
