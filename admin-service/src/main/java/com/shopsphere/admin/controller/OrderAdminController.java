package com.shopsphere.admin.controller;

import com.shopsphere.admin.dto.OrderDto;
import com.shopsphere.admin.dto.OrderStatusUpdateDto;
import com.shopsphere.admin.service.OrderAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class OrderAdminController {

    private final OrderAdminService orderAdminService;

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders(
            @RequestParam(required = false) String status) {
        if (status != null && !status.isBlank()) {
            return ResponseEntity.ok(orderAdminService.getOrdersByStatus(status));
        }
        return ResponseEntity.ok(orderAdminService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderAdminService.getOrderById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDto> updateStatus(@PathVariable Long id,
                                                 @RequestBody OrderStatusUpdateDto dto) {
        return ResponseEntity.ok(orderAdminService.updateOrderStatus(id, dto.getStatus()));
    }
}
