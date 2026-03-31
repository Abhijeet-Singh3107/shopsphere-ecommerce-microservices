package com.shopsphere.order.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService{
    @Override
    public String processPayment(String userEmail, BigDecimal amount) {
        return "MOCK-PAY-" + UUID.randomUUID().toString().toUpperCase().substring(0, 8);
    }
}
