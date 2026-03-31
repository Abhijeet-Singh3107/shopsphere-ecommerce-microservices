package com.shopsphere.admin.client;

import com.shopsphere.admin.dto.ProductDto;
import com.shopsphere.admin.dto.ProductRequestDto;
import com.shopsphere.admin.exception.ResourceNotFoundException;
import com.shopsphere.admin.exception.ServiceCommunicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CatalogServiceClient {

    private final WebClient.Builder webClientBuilder;
    private static final String BASE_URL = "http://catalog-service";

    public List<ProductDto> getAllProducts(int page, int size) {
        Map<String, Object> response = webClientBuilder.build()
                .get()
                .uri(BASE_URL + "/catalog/products?page=" + page + "&size=" + size)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, r ->
                        Mono.error(new ResourceNotFoundException("No products found")))
                .onStatus(HttpStatusCode::is5xxServerError, r ->
                        Mono.error(new ServiceCommunicationException("catalog-service", "Server error")))
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .onErrorMap(e -> !(e instanceof ResourceNotFoundException),
                        e -> new ServiceCommunicationException("catalog-service", e.getMessage()))
                .block();

        return (List<ProductDto>) response.get("content");
    }

    public ProductDto getProductById(Long id) {
        return webClientBuilder.build()
                .get()
                .uri(BASE_URL + "/catalog/products/" + id)
                .retrieve()
                .onStatus(status -> status.value() == 404, r ->
                        Mono.error(new ResourceNotFoundException("Product not found with id: " + id)))
                .onStatus(HttpStatusCode::is5xxServerError, r ->
                        Mono.error(new ServiceCommunicationException("catalog-service", "Server error")))
                .bodyToMono(ProductDto.class)
                .onErrorMap(e -> !(e instanceof ResourceNotFoundException),
                        e -> new ServiceCommunicationException("catalog-service", e.getMessage()))
                .block();
    }

    public ProductDto createProduct(ProductRequestDto requestDto) {
        return webClientBuilder.build()
                .post()
                .uri(BASE_URL + "/catalog/products")
                .bodyValue(requestDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, r ->
                        Mono.error(new ResourceNotFoundException("Invalid product data")))
                .onStatus(HttpStatusCode::is5xxServerError, r ->
                        Mono.error(new ServiceCommunicationException("catalog-service", "Server error")))
                .bodyToMono(ProductDto.class)
                .onErrorMap(e -> !(e instanceof ResourceNotFoundException),
                        e -> new ServiceCommunicationException("catalog-service", e.getMessage()))
                .block();
    }

    public ProductDto updateProduct(Long id, ProductRequestDto requestDto) {
        return webClientBuilder.build()
                .put()
                .uri(BASE_URL + "/catalog/products/" + id)
                .bodyValue(requestDto)
                .retrieve()
                .onStatus(status -> status.value() == 404, r ->
                        Mono.error(new ResourceNotFoundException("Product not found with id: " + id)))
                .onStatus(HttpStatusCode::is5xxServerError, r ->
                        Mono.error(new ServiceCommunicationException("catalog-service", "Server error")))
                .bodyToMono(ProductDto.class)
                .onErrorMap(e -> !(e instanceof ResourceNotFoundException),
                        e -> new ServiceCommunicationException("catalog-service", e.getMessage()))
                .block();
    }

    public void deleteProduct(Long id) {
        webClientBuilder.build()
                .delete()
                .uri(BASE_URL + "/catalog/products/" + id)
                .retrieve()
                .onStatus(status -> status.value() == 404, r ->
                        Mono.error(new ResourceNotFoundException("Product not found with id: " + id)))
                .onStatus(HttpStatusCode::is5xxServerError, r ->
                        Mono.error(new ServiceCommunicationException("catalog-service", "Server error")))
                .bodyToMono(Void.class)
                .onErrorMap(e -> !(e instanceof ResourceNotFoundException),
                        e -> new ServiceCommunicationException("catalog-service", e.getMessage()))
                .block();
    }

    public long getTotalProductCount() {
        Map<String, Object> response = webClientBuilder.build()
                .get()
                .uri(BASE_URL + "/catalog/products?page=0&size=1")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, r ->
                        Mono.error(new ResourceNotFoundException("No products found")))
                .onStatus(HttpStatusCode::is5xxServerError, r ->
                        Mono.error(new ServiceCommunicationException("catalog-service", "Server error")))
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .onErrorMap(e -> !(e instanceof ResourceNotFoundException),
                        e -> new ServiceCommunicationException("catalog-service", e.getMessage()))
                .block();

        Object total = response.get("totalElements");
        return total instanceof Number ? ((Number) total).longValue() : 0L;
    }
}