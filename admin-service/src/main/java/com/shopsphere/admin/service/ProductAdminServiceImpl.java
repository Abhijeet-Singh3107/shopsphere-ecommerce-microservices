package com.shopsphere.admin.service;

import com.shopsphere.admin.client.CatalogServiceClient;
import com.shopsphere.admin.dto.ProductDto;
import com.shopsphere.admin.dto.ProductRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductAdminServiceImpl implements ProductAdminService{
    private final CatalogServiceClient catalogServiceClient;

    @Override
    public List<ProductDto> getAllProducts(int page, int size) {
        return catalogServiceClient.getAllProducts(page, size);
    }

    @Override
    public ProductDto getProductById(Long id) {
        return catalogServiceClient.getProductById(id);
    }

    @Override
    public ProductDto createProduct(ProductRequestDto dto) {
        return catalogServiceClient.createProduct(dto);
    }

    @Override
    public ProductDto updateProduct(Long id, ProductRequestDto dto) {
        return catalogServiceClient.updateProduct(id, dto);
    }

    @Override
    public void deleteProduct(Long id) {
        catalogServiceClient.deleteProduct(id);
    }
}
