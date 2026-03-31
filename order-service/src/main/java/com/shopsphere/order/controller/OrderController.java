package com.shopsphere.order.controller;

import com.shopsphere.order.dto.CheckoutRequestDto;
import com.shopsphere.order.dto.OrderResponseDto;
import com.shopsphere.order.enums.OrderStatus;
import com.shopsphere.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponseDto> checkout(
            @RequestHeader("X-User-Email") String email,
            @RequestBody CheckoutRequestDto request) {
        return ResponseEntity.ok(orderService.checkout(email, request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getMyOrders(
            @RequestHeader("X-User-Email") String email) {
        return ResponseEntity.ok(orderService.getMyOrders(email));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(email, orderId));
    }

    @DeleteMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponseDto> cancelOrder(
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(email, orderId));
    }

    // admin purpose
    @GetMapping("/all")
    public ResponseEntity<List<OrderResponseDto>> getAllOrdersAdmin() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/admin/{orderId}/status")
    public ResponseEntity<OrderResponseDto> updateStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateStatus(orderId, status));
    }

    // GET single order by id (no ownership check)
    @GetMapping("/admin/{id}")
    public ResponseEntity<OrderResponseDto> getOrderByIdAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
}
