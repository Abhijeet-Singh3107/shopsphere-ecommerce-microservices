package com.shopsphere.order.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class CartItemResponseDto {
    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}
