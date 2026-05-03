package com.shopsphere.order.service;

import com.shopsphere.order.Client.CatalogServiceClient;
import com.shopsphere.order.dto.AddToCartRequestDto;
import com.shopsphere.order.dto.CartItemResponseDto;
import com.shopsphere.order.dto.CartResponseDto;
import com.shopsphere.order.dto.UpdateCartItemRequestDto;
import com.shopsphere.order.entity.Cart;
import com.shopsphere.order.entity.CartItem;
import com.shopsphere.order.exception.OrderException;
import com.shopsphere.order.exception.ResourceNotFoundException;
import com.shopsphere.order.repository.CartItemRepository;
import com.shopsphere.order.repository.CartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CatalogServiceClient catalogClient;

    @Override
    public CartResponseDto getCart(String userEmail) {
        Cart cart = getOrCreateCart(userEmail);
        return mapToDto(cart);
    }

    @Override
    @Transactional
    public CartResponseDto addItem(String userEmail, AddToCartRequestDto request) {
        // Validate product exists and has stock
        Map<String, Object> product = catalogClient.getProductDetails(request.getProductId());
        Integer stock = (Integer) product.get("stock");
        if (stock == null || stock < request.getQuantity()) {
            throw new OrderException("Insufficient stock for product id: " + request.getProductId());
        }

        Cart cart = getOrCreateCart(userEmail);

        Optional<CartItem> existingItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), request.getProductId());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQty = item.getQuantity() + request.getQuantity();
            if (newQty > stock) {
                throw new OrderException("Cannot add more than available stock.");
            }
            item.setQuantity(newQty);
            cartItemRepository.save(item);
        } else {
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .productId(request.getProductId())
                    .productName(catalogClient.getProductName(product))
                    .price(catalogClient.getProductPrice(product))
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(item);
        }

        Cart saved = cartRepository.save(cart);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public CartResponseDto updateItem(String userEmail, Long itemId, UpdateCartItemRequestDto request) {
        Cart cart = getOrCreateCart(userEmail);
        CartItem item = cartItemRepository.findById(itemId)
                .filter(i -> i.getCart().getId().equals(cart.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + itemId));

        if (request.getQuantity() <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(request.getQuantity());
            cartItemRepository.save(item);
        }

        return mapToDto(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public void removeItem(String userEmail, Long itemId) {
        Cart cart = getOrCreateCart(userEmail);
        CartItem item = cartItemRepository.findById(itemId)
                .filter(i -> i.getCart().getId().equals(cart.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + itemId));
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
    }

    @Override
    @Transactional
    public void clearCart(String userEmail) {
        Cart cart = getOrCreateCart(userEmail);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    // ---- helpers ----

    private Cart getOrCreateCart(String userEmail) {
        return cartRepository.findByUserEmail(userEmail)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder().userEmail(userEmail).build()));
    }

    private CartResponseDto mapToDto(Cart cart) {
        List<CartItemResponseDto> itemDtos = cart.getItems().stream()
                .map(i -> CartItemResponseDto.builder()
                        .id(i.getId())
                        .productId(i.getProductId())
                        .productName(i.getProductName())
                        .price(i.getPrice())
                        .quantity(i.getQuantity())
                        .subtotal(i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                        .build())
                .collect(Collectors.toList());

        BigDecimal total = itemDtos.stream()
                .map(CartItemResponseDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponseDto.builder()
                .id(cart.getId())
                .userEmail(cart.getUserEmail())
                .items(itemDtos)
                .totalAmount(total)
                .build();
    }
}
