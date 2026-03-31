package com.shopsphere.catalog.service;

import com.shopsphere.catalog.dto.ProductRequestDto;
import com.shopsphere.catalog.dto.ProductResponseDto;
import com.shopsphere.catalog.entity.Category;
import com.shopsphere.catalog.entity.Product;
import com.shopsphere.catalog.exception.ResourceNotFoundException;
import com.shopsphere.catalog.repository.CategoryRepository;
import com.shopsphere.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ProductResponseDto createProduct(ProductRequestDto request) {
        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: "
                                + request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .featured(request.getFeatured() != null
                        && request.getFeatured())
                .category(category)
                .build();

        return mapToDto(productRepository.save(product));
    }

    @Override
    public Page<ProductResponseDto> getAllProducts(int page,
                                                   int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(sortBy).ascending());
        return productRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id));
        return mapToDto(product);
    }

    @Override
    public Page<ProductResponseDto> searchProducts(String keyword,
                                                   int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository
                .searchProducts(keyword, pageable)
                .map(this::mapToDto);
    }

    @Override
    public Page<ProductResponseDto> getProductsByCategory(
            Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository
                .findByCategoryId(categoryId, pageable)
                .map(this::mapToDto);
    }

    @Override
    public Page<ProductResponseDto> getProductsByPriceRange(
            Double min, Double max, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository
                .findByPriceBetween(min, max, pageable)
                .map(this::mapToDto);
    }

    @Override
    public List<ProductResponseDto> getFeaturedProducts() {
        return productRepository.findByFeaturedTrue()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDto updateProduct(Long id,
                                            ProductRequestDto request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id));

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: "
                                + request.getCategoryId()));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        product.setFeatured(request.getFeatured() != null
                && request.getFeatured());
        product.setCategory(category);

        return mapToDto(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductResponseDto mapToDto(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getImageUrl(),
                product.getFeatured(),
                product.getCategory().getName()
        );
    }
}
