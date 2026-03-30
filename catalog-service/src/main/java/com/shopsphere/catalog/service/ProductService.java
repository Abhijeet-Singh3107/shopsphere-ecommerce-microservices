package com.shopsphere.catalog.service;

import com.shopsphere.catalog.dto.ProductRequestDto;
import com.shopsphere.catalog.dto.ProductResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    ProductResponseDto createProduct(ProductRequestDto request);

    Page<ProductResponseDto> getAllProducts(int page, int size, String sortBy);

    ProductResponseDto getProductById(Long id);

    Page<ProductResponseDto> searchProducts(String keyword, int page, int size);

    Page<ProductResponseDto> getProductsByCategory(Long categoryId, int page, int size);

    Page<ProductResponseDto> getProductsByPriceRange(Double min, Double max, int page, int size);

    List<ProductResponseDto> getFeaturedProducts();

    ProductResponseDto updateProduct(Long id, ProductRequestDto request);

    void deleteProduct(Long id);
}
