package com.shopsphere.order.service;

import com.shopsphere.order.dto.CheckoutRequestDto;
import com.shopsphere.order.entity.Cart;
import com.shopsphere.order.exception.OrderException;
import com.shopsphere.order.exception.ResourceNotFoundException;
import com.shopsphere.order.repository.CartRepository;
import com.shopsphere.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private PaymentService paymentService;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void checkout_emptyCart_throwsOrderException() {
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());

        when(cartRepository.findByUserEmail("user@test.com")).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> orderService.checkout("user@test.com", new CheckoutRequestDto()))
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("empty cart");
    }

    @Test
    void getOrderById_notFound_throwsException() {
        when(orderRepository.findByIdAndUserEmail(99L, "user@test.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById("user@test.com", 99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}