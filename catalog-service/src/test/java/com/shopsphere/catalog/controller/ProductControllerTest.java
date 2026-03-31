package com.shopsphere.catalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.catalog.dto.ProductRequestDto;
import com.shopsphere.catalog.dto.ProductResponseDto;
import com.shopsphere.catalog.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    // Add @MockBeans for any security beans if needed (JwtUtil, etc.)

    @Test
    void getProductById_returns200() throws Exception {
        ProductResponseDto response = new ProductResponseDto(
                1L, "Laptop", "Good laptop", 999.99, 10, null, false, "Electronics");

        when(productService.getProductById(1L)).thenReturn(response);

        mockMvc.perform(get("/catalog/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void createProduct_returns200() throws Exception {
        ProductRequestDto request = new ProductRequestDto();
        ProductResponseDto response = new ProductResponseDto(
                1L, "Laptop", "Good laptop", 999.99, 10, null, false, "Electronics");

        when(productService.createProduct(any(ProductRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/catalog/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"));
    }
}
