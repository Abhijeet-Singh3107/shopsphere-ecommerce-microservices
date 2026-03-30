package com.shopsphere.order.Client;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class CatalogServiceClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${catalog.service.url}")
    private String catalogServiceUrl;

    /**
     * Fetches product details (name + price) from catalog-service.
     * Returns a simple map: { "name": "...", "price": ... }
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getProductDetails(Long productId) {
        return webClientBuilder.build()
                .get()
                .uri(catalogServiceUrl + "/catalog/products/{id}", productId)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    /**
     * Returns the current stock for a product.
     */
    @SuppressWarnings("unchecked")
    public Integer getProductStock(Long productId) {
        Map<String, Object> product = getProductDetails(productId);
        return (Integer) product.get("stock");
    }

    public String getProductName(Map<String, Object> product) {
        return (String) product.get("name");
    }

    public BigDecimal getProductPrice(Map<String, Object> product) {
        Object price = product.get("price");
        if (price instanceof BigDecimal) return (BigDecimal) price;
        return new BigDecimal(price.toString());
    }
}
