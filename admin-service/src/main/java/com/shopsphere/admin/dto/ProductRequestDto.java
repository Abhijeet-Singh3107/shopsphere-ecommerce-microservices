package com.shopsphere.admin.dto;
import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductRequestDto {
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private Boolean featured;
    private Long categoryId;
}
