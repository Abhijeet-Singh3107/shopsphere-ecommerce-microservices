package com.shopsphere.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.order.dto.CheckoutRequestDto;
import com.shopsphere.order.dto.OrderResponseDto;
import com.shopsphere.order.enums.OrderStatus;
import com.shopsphere.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private OrderService orderService;


    @Test
    void getMyOrders_returns200() throws Exception {
        when(orderService.getMyOrders("user@test.com")).thenReturn(List.of());

        mockMvc.perform(get("/order/orders")
                        .header("X-User-Email", "user@test.com"))
                .andExpect(status().isOk());
    }

    @Test
    void checkout_returns200() throws Exception {
        CheckoutRequestDto request = new CheckoutRequestDto();
        request.setShippingAddress("123 Main St");

        OrderResponseDto response = OrderResponseDto.builder()
                .id(1L)
                .userEmail("user@test.com")
                .status(OrderStatus.PAID)
                .build();

        when(orderService.checkout(eq("user@test.com"), any(CheckoutRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/order/orders/checkout")
                        .header("X-User-Email", "user@test.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));
    }
}