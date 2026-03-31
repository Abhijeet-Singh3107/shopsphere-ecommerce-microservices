package com.shopsphere.order.service;

import com.shopsphere.order.dto.AddToCartRequestDto;
import com.shopsphere.order.dto.CartResponseDto;
import com.shopsphere.order.dto.UpdateCartItemRequestDto;

public interface CartService {

    CartResponseDto getCart(String userEmail);

    CartResponseDto addItem(String userEmail, AddToCartRequestDto request);

    CartResponseDto updateItem(String userEmail, Long itemId, UpdateCartItemRequestDto request);

    void removeItem(String userEmail, Long itemId);

    void clearCart(String userEmail);
}
