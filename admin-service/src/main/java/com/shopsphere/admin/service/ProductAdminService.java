package com.shopsphere.admin.service;

import com.shopsphere.admin.dto.ProductDto;
import com.shopsphere.admin.dto.ProductRequestDto;

import java.util.List;

public interface ProductAdminService {

    List<ProductDto> getAllProducts(int page, int size);

    ProductDto getProductById(Long id);

    ProductDto createProduct(ProductRequestDto dto);

    ProductDto updateProduct(Long id, ProductRequestDto dto);

    void deleteProduct(Long id);
}
