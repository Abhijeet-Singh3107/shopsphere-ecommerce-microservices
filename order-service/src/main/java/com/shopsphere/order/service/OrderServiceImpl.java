package com.shopsphere.order.service;

import com.shopsphere.order.config.RabbitMQConfig;
import com.shopsphere.order.dto.CheckoutRequestDto;
import com.shopsphere.order.dto.OrderItemResponseDto;
import com.shopsphere.order.dto.OrderResponseDto;
import com.shopsphere.order.entity.Cart;
import com.shopsphere.order.entity.Order;
import com.shopsphere.order.entity.OrderItem;
import com.shopsphere.order.enums.OrderStatus;
import com.shopsphere.order.event.OrderPlacedEvent;
import com.shopsphere.order.event.OrderStatusChangedEvent;
import com.shopsphere.order.exception.OrderException;
import com.shopsphere.order.exception.ResourceNotFoundException;
import com.shopsphere.order.repository.CartRepository;
import com.shopsphere.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final PaymentService paymentService;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public OrderResponseDto checkout(String userEmail, CheckoutRequestDto request) {
        Cart cart = cartRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new OrderException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new OrderException("Cannot checkout with an empty cart");
        }

        // Build order items from cart
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(ci -> OrderItem.builder()
                        .productId(ci.getProductId())
                        .productName(ci.getProductName())
                        .price(ci.getPrice())
                        .quantity(ci.getQuantity())
                        .subtotal(ci.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                        .build())
                .collect(Collectors.toList());

        BigDecimal total = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Process (mock) payment
        String paymentRef = paymentService.processPayment(userEmail, total);

        Order order = Order.builder()
                .userEmail(userEmail)
                .status(OrderStatus.PAID)
                .totalAmount(total)
                .shippingAddress(request.getShippingAddress())
                .paymentReference(paymentRef)
                .items(orderItems)
                .build();

        // Link items back to order
        orderItems.forEach(i -> i.setOrder(order));

        Order saved = orderRepository.save(order);

        // Publish RabbitMQ event
        OrderPlacedEvent event = new OrderPlacedEvent(saved.getId(), userEmail);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, event);

        // Clear cart after successful order
        cart.getItems().clear();
        cartRepository.save(cart);

        return mapToDto(saved);
    }

    @Override
    public List<OrderResponseDto> getMyOrders(String userEmail) {
        return orderRepository.findByUserEmailOrderByCreatedAtDesc(userEmail)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public OrderResponseDto getOrderById(String userEmail, Long orderId) {
        Order order = orderRepository.findByIdAndUserEmail(orderId, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        return mapToDto(order);
    }

    @Override
    @Transactional
    public OrderResponseDto cancelOrder(String userEmail, Long orderId) {
        Order order = orderRepository.findByIdAndUserEmail(orderId, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.DRAFT && order.getStatus() != OrderStatus.PAID) {
            throw new OrderException("Cannot cancel order in status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        return mapToDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponseDto updateStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        order.setStatus(status);
        Order saved = orderRepository.save(order);

        OrderStatusChangedEvent event = new OrderStatusChangedEvent(
                saved.getId(), saved.getUserEmail(), status.name());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.STATUS_ROUTING_KEY, event);

        return mapToDto(saved);
    }

    // ---- helper ----

    private OrderResponseDto mapToDto(Order order) {
        List<OrderItemResponseDto> itemDtos = order.getItems().stream()
                .map(i -> OrderItemResponseDto.builder()
                        .id(i.getId())
                        .productId(i.getProductId())
                        .productName(i.getProductName())
                        .price(i.getPrice())
                        .quantity(i.getQuantity())
                        .subtotal(i.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return OrderResponseDto.builder()
                .id(order.getId())
                .userEmail(order.getUserEmail())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .paymentReference(order.getPaymentReference())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(itemDtos)
                .build();
    }


    // admin purpose
    @Override
    @Transactional
    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll()
                .stream().map(this::mapToDto).toList();
    }

    @Override
    @Transactional
    public OrderResponseDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return mapToDto(order);
    }
}
