package com.shopsphere.catalog.service;

import com.shopsphere.catalog.dto.CategoryRequestDto;
import com.shopsphere.catalog.dto.CategoryResponseDto;
import com.shopsphere.catalog.entity.Category;
import com.shopsphere.catalog.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock private CategoryRepository categoryRepository;

    @InjectMocks private CategoryServiceImpl categoryService;

    @Test
    void createCategory_success() {
        CategoryRequestDto request = new CategoryRequestDto();
        request.setName("Electronics");
        request.setDescription("Gadgets");
        Category saved = Category.builder().id(1L).name("Electronics").description("Gadgets").build();

        when(categoryRepository.existsByName("Electronics")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        CategoryResponseDto result = categoryService.createCategory(request);

        assertThat(result.getName()).isEqualTo("Electronics");
    }

    @Test
    void createCategory_duplicateName_throwsException() {
        CategoryRequestDto request = new CategoryRequestDto();
        request.setName("Electronics");

        when(categoryRepository.existsByName("Electronics")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.createCategory(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Electronics");
    }
}
