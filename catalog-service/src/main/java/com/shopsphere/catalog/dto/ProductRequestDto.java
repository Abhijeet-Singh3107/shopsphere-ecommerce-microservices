package com.shopsphere.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ProductRequestDto {
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private Boolean featured;
    private Long categoryId;
}
