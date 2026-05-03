package com.shopsphere.admin.client;

import com.shopsphere.admin.dto.OrderDto;
import com.shopsphere.admin.dto.OrderStatusUpdateDto;
import com.shopsphere.admin.exception.ResourceNotFoundException;
import com.shopsphere.admin.exception.ServiceCommunicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderServiceClient {

    private final WebClient.Builder webClientBuilder;
    private static final String BASE_URL = "http://order-service";

    public List<OrderDto> getAllOrders() {
        return webClientBuilder.build()
                .get()
                .uri(BASE_URL + "/order/orders/all")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new ResourceNotFoundException("No orders found")))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new ServiceCommunicationException("order-service", "Server error")))
                .bodyToFlux(OrderDto.class)
                .collectList()
                .onErrorMap(e -> !(e instanceof ResourceNotFoundException),
                        e -> new ServiceCommunicationException("order-service", e.getMessage()))
                .block();
    }

    public OrderDto getOrderById(Long id) {
        return webClientBuilder.build()
                .get()
                .uri(BASE_URL + "/order/orders/admin/" + id)
                .retrieve()
                .onStatus(status -> status.value() == 404, response ->
                        Mono.error(new ResourceNotFoundException("Order not found with id: " + id)))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new ServiceCommunicationException("order-service", "Server error")))
                .bodyToMono(OrderDto.class)
                .onErrorMap(e -> !(e instanceof ResourceNotFoundException),
                        e -> new ServiceCommunicationException("order-service", e.getMessage()))
                .block();
    }

    public OrderDto updateOrderStatus(Long orderId, String status) {
        return webClientBuilder.build()
                .put()
                .uri(BASE_URL + "/order/orders/" + orderId + "/status")
                .bodyValue(new OrderStatusUpdateDto(status))
                .retrieve()
                .onStatus(status2 -> status2.value() == 404, response ->
                        Mono.error(new ResourceNotFoundException("Order not found with id: " + orderId)))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new ServiceCommunicationException("order-service", "Server error")))
                .bodyToMono(OrderDto.class)
                .onErrorMap(e -> !(e instanceof ResourceNotFoundException),
                        e -> new ServiceCommunicationException("order-service", e.getMessage()))
                .block();
    }

    public List<OrderDto> getOrdersByStatus(String status) {
        return webClientBuilder.build()
                .get()
                .uri(BASE_URL + "/order/orders/all?status=" + status)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new ResourceNotFoundException("No orders found with status: " + status)))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new ServiceCommunicationException("order-service", "Server error")))
                .bodyToFlux(OrderDto.class)
                .collectList()
                .onErrorMap(e -> !(e instanceof ResourceNotFoundException),
                        e -> new ServiceCommunicationException("order-service", e.getMessage()))
                .block();
    }
}