package com.shopsphere.catalog.repository;

import com.shopsphere.catalog.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product , Long> {


    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> searchProducts(@Param("keyword") String keyword,
                                 Pageable pageable);

    // filter by category with pagination
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    // get featured products
    List<Product> findByFeaturedTrue();

    // filter by price range
    Page<Product> findByPriceBetween(Double minPrice,
                                     Double maxPrice,
                                     Pageable pageable);
}
