package com.shopsphere.order.controller;

import com.shopsphere.order.dto.AddToCartRequestDto;
import com.shopsphere.order.dto.CartResponseDto;
import com.shopsphere.order.dto.UpdateCartItemRequestDto;
import com.shopsphere.order.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/order/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Gateway injects X-User-Email from the validated JWT
    private String getEmail(String header) { return header; }

    @GetMapping
    public ResponseEntity<CartResponseDto> getCart(
            @RequestHeader("X-User-Email") String email) {
        return ResponseEntity.ok(cartService.getCart(email));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponseDto> addItem(
            @RequestHeader("X-User-Email") String email,
            @RequestBody AddToCartRequestDto request) {
        return ResponseEntity.ok(cartService.addItem(email, request));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponseDto> updateItem(
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long itemId,
            @RequestBody UpdateCartItemRequestDto request) {
        return ResponseEntity.ok(cartService.updateItem(email, itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeItem(
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long itemId) {
        cartService.removeItem(email, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            @RequestHeader("X-User-Email") String email) {
        cartService.clearCart(email);
        return ResponseEntity.noContent().build();
    }
}
